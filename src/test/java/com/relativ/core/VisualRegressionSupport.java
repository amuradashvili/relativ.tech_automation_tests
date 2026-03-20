package com.relativ.core;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class VisualRegressionSupport {

    private static final String VISUAL_FREEZE_SCRIPT = """
            if (!document.getElementById('codex-visual-regression-style')) {
              const style = document.createElement('style');
              style.id = 'codex-visual-regression-style';
              style.textContent = `
                *,
                *::before,
                *::after {
                  animation: none !important;
                  transition: none !important;
                  caret-color: transparent !important;
                  scroll-behavior: auto !important;
                }
              `;
              document.head.appendChild(style);
            }
            document.querySelectorAll('canvas[aria-hidden="true"]').forEach((element) => {
              element.style.visibility = 'hidden';
            });
            document.querySelectorAll('[class*="animate-"]').forEach((element) => {
              element.style.animation = 'none';
              element.style.transform = 'none';
            });
            document.documentElement.style.scrollBehavior = 'auto';
            document.body.style.scrollBehavior = 'auto';
            window.scrollTo(0, 0);
            """;

    private static final String FULL_PAGE_HEIGHT_SCRIPT = """
            return Math.max(
              document.body.scrollHeight,
              document.body.offsetHeight,
              document.documentElement.clientHeight,
              document.documentElement.scrollHeight,
              document.documentElement.offsetHeight
            );
            """;

    private final WebDriver driver;
    private final UiConfig config;

    public VisualRegressionSupport(WebDriver driver, UiConfig config) {
        this.driver = driver;
        this.config = config;
    }

    public void assertPageMatches(String checkpointName) {
        try {
            Files.createDirectories(config.visualBaselineDir());
            Files.createDirectories(config.visualArtifactDir());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create visual regression directories.", exception);
        }

        TestExecutionLog.step("Capture visual checkpoint " + checkpointName);
        Path baselineFile = config.visualBaselineDir().resolve(checkpointName + ".png");
        Path actualFile = config.visualArtifactDir().resolve(checkpointName + "-actual.png");
        Path diffFile = config.visualArtifactDir().resolve(checkpointName + "-diff.png");

        BufferedImage actualImage = captureFullPage();
        writeImage(actualImage, actualFile);

        if (config.visualUpdateBaselines()) {
            writeImage(actualImage, baselineFile);
            deleteIfExists(diffFile);
            TestExecutionLog.step("Baseline updated for " + checkpointName);
            return;
        }

        if (Files.notExists(baselineFile)) {
            Assertions.fail("Missing visual baseline: " + baselineFile.toAbsolutePath()
                    + ". Run once with -Drelativ.visualUpdateBaselines=true to approve the current design.");
        }

        BufferedImage baselineImage = readImage(baselineFile);
        ComparisonResult comparison = compare(baselineImage, actualImage);
        if (comparison.diffPixels() > config.visualAllowedDiffPixels()) {
            writeImage(comparison.diffImage(), diffFile);
            Assertions.fail("Visual mismatch for checkpoint '" + checkpointName + "'. "
                    + "Diff pixels: " + comparison.diffPixels()
                    + ", allowed diff pixels: " + config.visualAllowedDiffPixels()
                    + ", color tolerance: " + config.visualColorTolerance()
                    + ". Baseline: " + baselineFile.toAbsolutePath()
                    + ", actual: " + actualFile.toAbsolutePath()
                    + ", diff: " + diffFile.toAbsolutePath());
        }

        deleteIfExists(diffFile);
        TestExecutionLog.step("Checkpoint matched baseline " + checkpointName);
    }

    private BufferedImage captureFullPage() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Dimension originalSize = driver.manage().window().getSize();

        try {
            driver.manage().window().setSize(new Dimension(config.visualViewportWidth(), config.visualViewportHeight()));
            waitForFonts();
            sleep(config.visualSettleMillis());
            js.executeScript(VISUAL_FREEZE_SCRIPT);
            sleep(100);

            Number pageHeightValue = (Number) js.executeScript(FULL_PAGE_HEIGHT_SCRIPT);
            int pageHeight = Math.max(pageHeightValue.intValue(), config.visualViewportHeight());

            driver.manage().window().setSize(new Dimension(config.visualViewportWidth(), pageHeight));
            js.executeScript("window.scrollTo(0, 0);");
            sleep(config.visualSettleMillis());

            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            return readImage(screenshot);
        } finally {
            driver.manage().window().setSize(originalSize);
        }
    }

    private void waitForFonts() {
        for (int attempt = 0; attempt < 20; attempt++) {
            try {
                Object fontsReady = ((JavascriptExecutor) driver)
                        .executeScript("return !document.fonts || document.fonts.status === 'loaded';");
                if (Boolean.TRUE.equals(fontsReady)) {
                    return;
                }
            } catch (RuntimeException ignored) {
                return;
            }
            sleep(100);
        }
    }

    private ComparisonResult compare(BufferedImage baselineImage, BufferedImage actualImage) {
        int width = Math.max(baselineImage.getWidth(), actualImage.getWidth());
        int height = Math.max(baselineImage.getHeight(), actualImage.getHeight());
        BufferedImage diffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = diffImage.createGraphics();
        graphics.drawImage(actualImage, 0, 0, null);

        int diffPixels = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean insideBaseline = x < baselineImage.getWidth() && y < baselineImage.getHeight();
                boolean insideActual = x < actualImage.getWidth() && y < actualImage.getHeight();

                if (!insideBaseline || !insideActual) {
                    diffImage.setRGB(x, y, new Color(255, 0, 255, 255).getRGB());
                    diffPixels++;
                    continue;
                }

                int baselineRgb = baselineImage.getRGB(x, y);
                int actualRgb = actualImage.getRGB(x, y);
                if (!isWithinTolerance(baselineRgb, actualRgb)) {
                    diffImage.setRGB(x, y, new Color(255, 0, 0, 255).getRGB());
                    diffPixels++;
                }
            }
        }

        graphics.dispose();
        return new ComparisonResult(diffPixels, diffImage);
    }

    private boolean isWithinTolerance(int baselineRgb, int actualRgb) {
        Color baseline = new Color(baselineRgb, true);
        Color actual = new Color(actualRgb, true);
        int tolerance = config.visualColorTolerance();
        return Math.abs(baseline.getRed() - actual.getRed()) <= tolerance
                && Math.abs(baseline.getGreen() - actual.getGreen()) <= tolerance
                && Math.abs(baseline.getBlue() - actual.getBlue()) <= tolerance
                && Math.abs(baseline.getAlpha() - actual.getAlpha()) <= tolerance;
    }

    private BufferedImage readImage(Path path) {
        try {
            BufferedImage image = ImageIO.read(path.toFile());
            if (image == null) {
                throw new IllegalStateException("Unreadable image file: " + path.toAbsolutePath());
            }
            return image;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read image file: " + path.toAbsolutePath(), exception);
        }
    }

    private BufferedImage readImage(byte[] bytes) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) {
                throw new IllegalStateException("Unable to decode screenshot bytes.");
            }
            return image;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to decode screenshot bytes.", exception);
        }
    }

    private void writeImage(BufferedImage image, Path path) {
        try {
            Files.createDirectories(path.getParent());
            ImageIO.write(image, "png", path.toFile());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to write image file: " + path.toAbsolutePath(), exception);
        }
    }

    private void deleteIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // Diff cleanup should not fail the test.
        }
    }

    private void sleep(int millis) {
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while stabilizing page for visual capture.", exception);
        }
    }

    private record ComparisonResult(int diffPixels, BufferedImage diffImage) {
    }
}

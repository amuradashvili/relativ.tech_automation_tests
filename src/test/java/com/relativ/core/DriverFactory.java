package com.relativ.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class DriverFactory {

    private static final List<Path> CHROMEDRIVER_CACHE_ROOTS = List.of(
            Path.of("target", "webdriver-cache", "chromedriver", "win64"),
            Path.of(System.getProperty("user.home"), ".cache", "selenium", "chromedriver", "win64")
    );

    private DriverFactory() {
    }

    public static WebDriver createChrome(UiConfig config) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--force-device-scale-factor=1");
        options.addArguments("--hide-scrollbars");
        if (config.headless()) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-debugging-port=0");
        } else {
            options.addArguments("--start-maximized");
        }

        resolveOfflineChromedriver()
                .ifPresentOrElse(
                        chromedriverPath -> System.setProperty("webdriver.chrome.driver", chromedriverPath.toString()),
                        () -> {
                            String cachePath = Path.of("target", "webdriver-cache").toAbsolutePath().toString();
                            WebDriverManager.chromedriver()
                                    .cachePath(cachePath)
                                    .resolutionCachePath(cachePath)
                                    .setup();
                        }
                );

        WebDriver driver = new ChromeDriver(options);
        if (!config.headless()) {
            driver.manage().window().maximize();
        }
        return driver;
    }

    private static Optional<Path> resolveOfflineChromedriver() {
        String configuredPath = UiConfig.read("relativ.chromedriverPath", "RELATIV_CHROMEDRIVER_PATH", "");
        if (!configuredPath.isBlank()) {
            Path candidate = Path.of(configuredPath);
            if (Files.exists(candidate)) {
                return Optional.of(candidate.toAbsolutePath());
            }
        }

        return CHROMEDRIVER_CACHE_ROOTS.stream()
                .filter(Files::exists)
                .flatMap(DriverFactory::walkChromedriverExecutables)
                .sorted(DriverFactory::compareByVersionDescending)
                .findFirst()
                .map(Path::toAbsolutePath);
    }

    private static Stream<Path> walkChromedriverExecutables(Path root) {
        try {
            return Files.walk(root)
                    .filter(path -> path.getFileName() != null && "chromedriver.exe".equalsIgnoreCase(path.getFileName().toString()));
        } catch (IOException ignored) {
            return Stream.empty();
        }
    }

    private static int compareByVersionDescending(Path left, Path right) {
        return compareVersions(versionParts(right), versionParts(left));
    }

    private static int compareVersions(int[] left, int[] right) {
        int length = Math.max(left.length, right.length);
        for (int index = 0; index < length; index++) {
            int leftPart = index < left.length ? left[index] : 0;
            int rightPart = index < right.length ? right[index] : 0;
            int comparison = Integer.compare(leftPart, rightPart);
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }

    private static int[] versionParts(Path chromedriverPath) {
        Path versionDirectory = chromedriverPath.getParent();
        if (versionDirectory == null || versionDirectory.getFileName() == null) {
            return new int[0];
        }

        String[] parts = versionDirectory.getFileName().toString().split("\\.");
        int[] parsed = new int[parts.length];
        for (int index = 0; index < parts.length; index++) {
            try {
                parsed[index] = Integer.parseInt(parts[index]);
            } catch (NumberFormatException ignored) {
                parsed[index] = 0;
            }
        }
        return parsed;
    }
}

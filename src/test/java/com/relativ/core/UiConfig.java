package com.relativ.core;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public final class UiConfig {

    private static final String DEFAULT_BASE_URL = "http://localhost:3000";

    private final String baseUrl;
    private final String loginUrl;
    private final boolean headless;
    private final String username;
    private final String password;
    private final List<String> clickKeys;
    private final int stayOpenSeconds;
    private final Path visualBaselineDir;
    private final Path visualArtifactDir;
    private final boolean visualUpdateBaselines;
    private final int visualColorTolerance;
    private final int visualAllowedDiffPixels;
    private final int visualViewportWidth;
    private final int visualViewportHeight;
    private final int visualSettleMillis;

    public UiConfig() {
        this.baseUrl = read("relativ.baseUrl", "RELATIV_BASE_URL", DEFAULT_BASE_URL);
        this.loginUrl = read("relativ.loginUrl", "RELATIV_LOGIN_URL", "");
        this.headless = Boolean.parseBoolean(read("relativ.headless", "RELATIV_HEADLESS", "true"));
        this.username = read("relativ.username", "RELATIV_USERNAME", "");
        this.password = read("relativ.password", "RELATIV_PASSWORD", "");
        this.clickKeys = Arrays.stream(read("relativ.clickKeys", "RELATIV_CLICK_KEYS", "").split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
        this.stayOpenSeconds = readInt("relativ.stayOpenSeconds", "RELATIV_STAY_OPEN_SECONDS", 10);
        this.visualBaselineDir = Path.of(read(
                "relativ.visualBaselineDir",
                "RELATIV_VISUAL_BASELINE_DIR",
                "src/test/resources/visual-baselines"
        ));
        this.visualArtifactDir = Path.of(read(
                "relativ.visualArtifactDir",
                "RELATIV_VISUAL_ARTIFACT_DIR",
                "target/visual-regression"
        ));
        this.visualUpdateBaselines = Boolean.parseBoolean(read(
                "relativ.visualUpdateBaselines",
                "RELATIV_VISUAL_UPDATE_BASELINES",
                "false"
        ));
        this.visualColorTolerance = readInt(
                "relativ.visualColorTolerance",
                "RELATIV_VISUAL_COLOR_TOLERANCE",
                0
        );
        this.visualAllowedDiffPixels = readInt(
                "relativ.visualAllowedDiffPixels",
                "RELATIV_VISUAL_ALLOWED_DIFF_PIXELS",
                0
        );
        this.visualViewportWidth = readInt(
                "relativ.visualViewportWidth",
                "RELATIV_VISUAL_VIEWPORT_WIDTH",
                1440
        );
        this.visualViewportHeight = readInt(
                "relativ.visualViewportHeight",
                "RELATIV_VISUAL_VIEWPORT_HEIGHT",
                900
        );
        this.visualSettleMillis = readInt(
                "relativ.visualSettleMillis",
                "RELATIV_VISUAL_SETTLE_MILLIS",
                400
        );
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String loginUrl() {
        return loginUrl;
    }

    public boolean headless() {
        return headless;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public List<String> clickKeys() {
        return clickKeys;
    }

    public int stayOpenSeconds() {
        return stayOpenSeconds;
    }

    public Path visualBaselineDir() {
        return visualBaselineDir;
    }

    public Path visualArtifactDir() {
        return visualArtifactDir;
    }

    public boolean visualUpdateBaselines() {
        return visualUpdateBaselines;
    }

    public int visualColorTolerance() {
        return visualColorTolerance;
    }

    public int visualAllowedDiffPixels() {
        return visualAllowedDiffPixels;
    }

    public int visualViewportWidth() {
        return visualViewportWidth;
    }

    public int visualViewportHeight() {
        return visualViewportHeight;
    }

    public int visualSettleMillis() {
        return visualSettleMillis;
    }

    public boolean hasCredentials() {
        return !username.isBlank() && !password.isBlank();
    }

    public static boolean hasCredentialsConfigured() {
        String username = read("relativ.username", "RELATIV_USERNAME", "");
        String password = read("relativ.password", "RELATIV_PASSWORD", "");
        return !username.isBlank() && !password.isBlank();
    }

    public static String read(String propertyName, String envName, String defaultValue) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            value = System.getenv(envName);
        }
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim();
    }

    private static int readInt(String propertyName, String envName, int defaultValue) {
        String value = read(propertyName, envName, String.valueOf(defaultValue));
        try {
            int parsed = Integer.parseInt(value);
            return Math.max(parsed, 0);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }
}

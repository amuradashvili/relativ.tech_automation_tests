package com.relativ.core;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class UiConfig {

    private static final String DEFAULT_LOCAL_BASE_URL = "http://localhost:3000";
    private static final String DEFAULT_REMOTE_BASE_URL = "https://www.relativ.tech";
    private static final Duration BASE_URL_CONNECT_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration BASE_URL_REQUEST_TIMEOUT = Duration.ofSeconds(4);
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(BASE_URL_CONNECT_TIMEOUT)
            .build();
    private static final List<String> RELATIV_MARKERS = List.of(
            "Architects Of Fintech Flow",
            "Relativ Tech"
    );

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
        this.baseUrl = resolveBaseUrl();
        this.loginUrl = read("relativ.loginUrl", "RELATIV_LOGIN_URL", "");
        this.headless = Boolean.parseBoolean(read("relativ.headless", "RELATIV_HEADLESS", "false"));
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

    private static String resolveBaseUrl() {
        String configuredBaseUrl = read("relativ.baseUrl", "RELATIV_BASE_URL", "");
        if (!configuredBaseUrl.isBlank()) {
            return configuredBaseUrl;
        }

        return candidateBaseUrls().stream()
                .filter(UiConfig::looksLikeRelativSite)
                .findFirst()
                .orElse(DEFAULT_LOCAL_BASE_URL);
    }

    private static List<String> candidateBaseUrls() {
        List<String> candidates = new ArrayList<>();
        for (int port = 3000; port <= 3005; port++) {
            candidates.add("http://localhost:" + port);
            candidates.add("http://127.0.0.1:" + port);
        }
        candidates.add(DEFAULT_REMOTE_BASE_URL);
        return candidates;
    }

    private static boolean looksLikeRelativSite(String baseUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl))
                    .timeout(BASE_URL_REQUEST_TIMEOUT)
                    .GET()
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400 || response.body() == null || response.body().isBlank()) {
                return false;
            }

            String pageBody = response.body().toLowerCase(Locale.ROOT);
            return RELATIV_MARKERS.stream()
                    .map(marker -> marker.toLowerCase(Locale.ROOT))
                    .anyMatch(pageBody::contains);
        } catch (IOException | InterruptedException | IllegalArgumentException ignored) {
            if (ignored instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return false;
        }
    }
}

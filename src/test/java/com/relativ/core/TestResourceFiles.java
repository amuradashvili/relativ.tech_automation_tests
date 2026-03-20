package com.relativ.core;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public final class TestResourceFiles {

    private TestResourceFiles() {
    }

    public static String absolutePath(String resourcePath) {
        URL resource = TestResourceFiles.class.getClassLoader().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalArgumentException("Test resource not found: " + resourcePath);
        }
        try {
            return Path.of(resource.toURI()).toAbsolutePath().toString();
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Invalid resource path: " + resourcePath, exception);
        }
    }
}

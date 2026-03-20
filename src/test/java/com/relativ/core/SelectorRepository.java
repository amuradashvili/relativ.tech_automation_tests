package com.relativ.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public final class SelectorRepository {

    private final Properties selectors = new Properties();

    public SelectorRepository() {
        this("selectors.properties");
    }

    public SelectorRepository(String resourceName) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new IllegalStateException("Selector file not found on classpath: " + resourceName);
            }
            selectors.load(input);
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to load selector file.", exception);
        }
    }

    public List<String> getRequiredSelectors(String key) {
        return getOptionalSelectors(key)
                .orElseThrow(() -> new IllegalArgumentException("Missing selector key: " + key));
    }

    public Optional<List<String>> getOptionalSelectors(String key) {
        String rawValue = selectors.getProperty(key);
        if (rawValue == null || rawValue.isBlank()) {
            return Optional.empty();
        }
        List<String> selectorList = Arrays.stream(rawValue.split(";"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
        if (selectorList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(selectorList);
    }
}


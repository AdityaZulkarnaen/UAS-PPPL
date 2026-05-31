package org.example.utils;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads configuration from src/test/resources/config.properties.
 * System properties (-Dkey=value) override file values so the suite can be
 * tuned from the command line / CI without editing the file.
 */
public final class ConfigReader {

    private static final Properties PROPS = load();

    private ConfigReader() {
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                        "config.properties not found on the test classpath. "
                                + "Copy config.properties.example to config.properties.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read config.properties", e);
        }
        return props;
    }

    public static String get(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override;
        }
        String value = PROPS.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing config key: " + key);
        }
        return value;
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key).trim());
    }

    public static String baseUrl() {
        return get("baseUrl");
    }

    public static String mapUrl() {
        return baseUrl() + get("mapPath");
    }

    public static String email() {
        return get("email");
    }

    public static String password() {
        return get("password");
    }

    public static String browser() {
        return get("browser");
    }

    public static boolean headless() {
        return getBoolean("headless");
    }

    public static int timeoutSeconds() {
        return getInt("timeoutSeconds");
    }
}

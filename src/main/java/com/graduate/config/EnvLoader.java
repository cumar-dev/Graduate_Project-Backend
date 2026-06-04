package com.graduate.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads backend/.env into system properties so {@code mvn spring-boot:run} works
 * without a batch script (Spring resolves ${MONGODB_URI} from env + system properties).
 */
public final class EnvLoader {

    private EnvLoader() {
    }

    public static void loadDotEnv() {
        Path envFile = findEnvFile();
        if (envFile == null) {
            return;
        }
        try {
            for (String line : Files.readAllLines(envFile)) {
                applyLine(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + envFile.toAbsolutePath(), e);
        }
    }

    /** Looks in cwd, then parent dirs (covers backend/ and repo root). */
    private static Path findEnvFile() {
        Path dir = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        for (int i = 0; i < 5 && dir != null; i++) {
            Path candidate = dir.resolve(".env");
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
            dir = dir.getParent();
        }
        return null;
    }

    private static void applyLine(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }
        int eq = trimmed.indexOf('=');
        if (eq <= 0) {
            return;
        }
        String key = trimmed.substring(0, eq).trim();
        String value = trimmed.substring(eq + 1).trim();
        if ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'"))) {
            value = value.substring(1, value.length() - 1);
        }
        if (System.getenv(key) == null && System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }
}

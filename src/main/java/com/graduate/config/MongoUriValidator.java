package com.graduate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

@Configuration
@Profile("dev")
public class MongoUriValidator {

    private final Environment environment;

    public MongoUriValidator(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    void validate() {
        String uri = environment.getProperty("spring.data.mongodb.uri");
        if (uri == null || uri.isBlank()) {
            throw new IllegalStateException(
                    "MONGODB_URI is not set. Create backend/.env with your Atlas connection string, "
                            + "or run from backend folder: run-backend.bat");
        }
        if (!uri.startsWith("mongodb://") && !uri.startsWith("mongodb+srv://")) {
            throw new IllegalStateException(
                    "Invalid MongoDB URI. It must start with mongodb:// or mongodb+srv://. "
                            + "Check backend/.env (no spaces, full Atlas URL).");
        }
    }
}

package com.graduate;

import com.graduate.config.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class GraduateProjectApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(GraduateProjectApplication.class);
    }

    public static void main(String[] args) {
        EnvLoader.loadDotEnv();
        SpringApplication.run(GraduateProjectApplication.class, args);
    }
}

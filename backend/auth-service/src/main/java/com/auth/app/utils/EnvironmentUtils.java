package com.auth.app.utils;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class EnvironmentUtils {

    private static Environment environment;

    public EnvironmentUtils(Environment env) {
        environment = env;
    }

    public static boolean isDev() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }

    public static boolean isProd() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    public static boolean isTest() {
        return Arrays.asList(environment.getActiveProfiles()).contains("test");
    }
}


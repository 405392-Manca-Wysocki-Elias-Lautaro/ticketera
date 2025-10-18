package com.auth.app.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FrontendUrlBuilder {

    @Value("${frontend.url}")
    private String baseUrl;

    public String buildLoginUrl() {
        return baseUrl + "/login";
    }

    public String buildResetPasswordUrl(String token) {
        return baseUrl + "/reset-password?token=" + token;
    }

    public String buildVerifyEmailUrl(String token) {
        return baseUrl + "/verify-email?token=" + token;
    }
}


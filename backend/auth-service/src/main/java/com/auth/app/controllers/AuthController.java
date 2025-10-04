package com.auth.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.response.AuthResponse;
import com.auth.app.servicies.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        String userAgent = httpRequest.getHeader("User-Agent");
        String ipAddress = httpRequest.getRemoteAddr();

        AuthResponse response = authService.register(request, userAgent, ipAddress);
        return ResponseEntity.ok(response);
    }

}

package org.example.presentation.rest.dtos.auth;

public record AuthResponse(String accessToken, String tokenType, long expiresIn) {}

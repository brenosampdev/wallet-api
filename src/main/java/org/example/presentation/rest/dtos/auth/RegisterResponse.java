package org.example.presentation.rest.dtos.auth;

import java.util.UUID;

public record RegisterResponse(UUID id, String name, String email) {}

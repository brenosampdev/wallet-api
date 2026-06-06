package org.example.presentation.rest.dtos.user;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, String name, String email, BigDecimal balance, Instant createdAt) {}

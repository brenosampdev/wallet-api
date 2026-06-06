package org.example.presentation.rest.dtos.goal;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GoalResponse(
    UUID id,
    String title,
    String description,
    BigDecimal targetAmount,
    BigDecimal currentAmount,
    boolean completed,
    Instant createdAt
) {}

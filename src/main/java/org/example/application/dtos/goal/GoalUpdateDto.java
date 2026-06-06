package org.example.application.dtos.goal;

import java.math.BigDecimal;
import java.util.UUID;

public record GoalUpdateDto(UUID id, UUID userId, String title, String description, BigDecimal targetAmount) {}

package org.example.presentation.rest.dtos.goal;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record UpdateGoalRequest(
    @Size(max = 120) String title,
    @Size(max = 255) String description,
    @DecimalMin(value = "0.01") BigDecimal targetAmount
) {}

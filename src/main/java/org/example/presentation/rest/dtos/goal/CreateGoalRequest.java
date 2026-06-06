package org.example.presentation.rest.dtos.goal;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateGoalRequest(
    @NotBlank @Size(max = 120) String title,
    @Size(max = 255) String description,
    @NotNull @DecimalMin(value = "0.01") BigDecimal targetAmount
) {}

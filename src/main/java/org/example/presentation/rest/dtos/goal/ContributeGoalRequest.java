package org.example.presentation.rest.dtos.goal;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ContributeGoalRequest(@NotNull @DecimalMin(value = "0.01") BigDecimal amount) {}

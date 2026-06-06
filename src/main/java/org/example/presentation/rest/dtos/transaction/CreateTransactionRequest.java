package org.example.presentation.rest.dtos.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.example.domain.enums.transactions.TransactionType;

public record CreateTransactionRequest(
    @Schema(example = "OUTPUT") @NotNull TransactionType type,
    @Schema(example = "150.00") @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
    @NotNull UUID categoryId,
    @Schema(example = "2026-06-06T10:30:00Z") Instant dateTime,
    @Size(max = 255) String description,
    @Positive Integer installments
) {}

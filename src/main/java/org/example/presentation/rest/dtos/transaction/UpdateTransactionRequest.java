package org.example.presentation.rest.dtos.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.example.domain.enums.transactions.TransactionType;

public record UpdateTransactionRequest(
    TransactionType type,
    @DecimalMin(value = "0.01") BigDecimal amount,
    UUID categoryId,
    Instant dateTime,
    @Size(max = 255) String description,
    @Positive Integer installments
) {}

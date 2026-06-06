package org.example.presentation.rest.dtos.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.example.domain.enums.transactions.TransactionType;

public record TransactionResponse(
    UUID id,
    TransactionType type,
    BigDecimal amount,
    UUID categoryId,
    Instant dateTime,
    String description,
    Integer installments,
    Instant createdAt
) {}

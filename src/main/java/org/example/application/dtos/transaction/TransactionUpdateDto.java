package org.example.application.dtos.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.example.domain.enums.transactions.TransactionType;

public record TransactionUpdateDto(
    UUID id,
    UUID userId,
    TransactionType type,
    BigDecimal amount,
    UUID categoryId,
    Instant dateTime,
    String description,
    Integer installments
) {}

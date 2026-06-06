package org.example.application.dtos.transaction;

import java.time.Instant;
import java.util.UUID;

import org.example.domain.enums.transactions.TransactionType;

public record TransactionFilterDto(
    UUID userId,
    TransactionType type,
    UUID categoryId,
    Instant from,
    Instant to
) {}

package org.example.presentation.rest.dtos.category;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(UUID id, String title, String description, Instant createdAt) {}

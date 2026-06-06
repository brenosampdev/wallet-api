package org.example.application.dtos.category;

import java.util.UUID;

public record CategoryUpdateDto(UUID id, UUID userId, String title, String description) {}

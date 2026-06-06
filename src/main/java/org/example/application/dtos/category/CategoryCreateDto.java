package org.example.application.dtos.category;

import java.util.UUID;

public record CategoryCreateDto(UUID userId, String title, String description) {}

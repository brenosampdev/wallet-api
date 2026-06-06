package org.example.presentation.rest.dtos.category;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
    @Size(max = 80) String title,
    @Size(max = 255) String description
) {}

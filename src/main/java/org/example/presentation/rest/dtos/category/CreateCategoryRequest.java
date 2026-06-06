package org.example.presentation.rest.dtos.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
    @Schema(example = "Alimentação") @NotBlank @Size(max = 80) String title,
    @Schema(example = "Gastos com comida") @Size(max = 255) String description
) {}

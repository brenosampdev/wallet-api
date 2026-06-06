package org.example.presentation.rest.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Schema(example = "breno@example.com") @NotBlank @Email String email,
    @Schema(example = "senha-forte-123") @NotBlank String password
) {}

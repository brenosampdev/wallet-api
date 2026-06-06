package org.example.presentation.rest.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @Schema(example = "Breno") @NotBlank @Size(max = 120) String name,
    @Schema(example = "breno@example.com") @NotBlank @Email @Size(max = 180) String email,
    @Schema(example = "senha-forte-123") @NotBlank @Size(min = 8, max = 128) String password
) {}

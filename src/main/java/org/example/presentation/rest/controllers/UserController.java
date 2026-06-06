package org.example.presentation.rest.controllers;

import org.example.application.dtos.user.UserUpdateDto;
import org.example.application.services.UserService;
import org.example.domain.entities.UserEntity;
import org.example.infrastructure.security.AuthenticatedUser;
import org.example.presentation.rest.dtos.user.UpdateUserRequest;
import org.example.presentation.rest.dtos.user.UserResponse;
import org.example.presentation.rest.mappers.RestMappers;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Perfil do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me")
  @Operation(summary = "Retorna o perfil do usuário autenticado")
  public UserResponse me(@AuthenticationPrincipal AuthenticatedUser principal) {
    UserEntity user = userService.getById(principal.userId());
    return RestMappers.toResponse(user);
  }

  @PatchMapping("/me")
  @Operation(summary = "Atualiza o nome do usuário autenticado")
  public UserResponse updateMe(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @Valid @RequestBody UpdateUserRequest request
  ) {
    UserEntity user = userService.update(new UserUpdateDto(principal.userId(), request.name()));
    return RestMappers.toResponse(user);
  }
}

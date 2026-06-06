package org.example.presentation.rest.controllers;

import org.example.application.dtos.auth.AuthTokenDto;
import org.example.application.dtos.auth.RegisterDto;
import org.example.application.services.AuthService;
import org.example.domain.entities.UserEntity;
import org.example.presentation.rest.dtos.auth.AuthResponse;
import org.example.presentation.rest.dtos.auth.LoginRequest;
import org.example.presentation.rest.dtos.auth.RegisterRequest;
import org.example.presentation.rest.dtos.auth.RegisterResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Cadastro e login de usuários")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  @Operation(summary = "Cadastra um novo usuário")
  @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
  public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
    UserEntity user = authService.register(new RegisterDto(request.name(), request.email(), request.password()));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new RegisterResponse(user.getId(), user.getName(), user.getEmail()));
  }

  @PostMapping("/login")
  @Operation(summary = "Autentica e devolve um token JWT")
  @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    AuthTokenDto token = authService.login(request.email(), request.password());
    return new AuthResponse(token.accessToken(), token.tokenType(), token.expiresIn());
  }
}

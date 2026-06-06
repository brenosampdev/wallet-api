package org.example.presentation.rest.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.example.application.dtos.goal.GoalCreateDto;
import org.example.application.dtos.goal.GoalUpdateDto;
import org.example.application.services.GoalService;
import org.example.domain.entities.GoalEntity;
import org.example.infrastructure.security.AuthenticatedUser;
import org.example.presentation.rest.dtos.goal.ContributeGoalRequest;
import org.example.presentation.rest.dtos.goal.CreateGoalRequest;
import org.example.presentation.rest.dtos.goal.GoalResponse;
import org.example.presentation.rest.dtos.goal.UpdateGoalRequest;
import org.example.presentation.rest.mappers.RestMappers;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/goals")
@Tag(name = "Goals", description = "Metas financeiras do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class GoalController {

  private final GoalService goalService;

  public GoalController(GoalService goalService) {
    this.goalService = goalService;
  }

  @PostMapping
  @Operation(summary = "Cria uma meta financeira")
  public ResponseEntity<GoalResponse> create(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @Valid @RequestBody CreateGoalRequest request
  ) {
    GoalEntity goal = goalService.create(new GoalCreateDto(
        principal.userId(), request.title(), request.description(), request.targetAmount()));
    return ResponseEntity.created(URI.create("/api/v1/goals/" + goal.getId()))
        .body(RestMappers.toResponse(goal));
  }

  @GetMapping
  @Operation(summary = "Lista metas do usuário")
  public List<GoalResponse> list(@AuthenticationPrincipal AuthenticatedUser principal) {
    return goalService.listForUser(principal.userId()).stream().map(RestMappers::toResponse).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Busca uma meta por id")
  @ApiResponse(responseCode = "404", description = "Meta não encontrada")
  public GoalResponse get(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @PathVariable UUID id
  ) {
    return RestMappers.toResponse(goalService.findByIdForUser(id, principal.userId()));
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Atualiza uma meta")
  @ApiResponse(responseCode = "404", description = "Meta não encontrada")
  public GoalResponse update(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @PathVariable UUID id,
      @Valid @RequestBody UpdateGoalRequest request
  ) {
    GoalEntity goal = goalService.update(new GoalUpdateDto(
        id, principal.userId(), request.title(), request.description(), request.targetAmount()));
    return RestMappers.toResponse(goal);
  }

  @PostMapping("/{id}/contribute")
  @Operation(summary = "Adiciona valor a uma meta; marca como concluída quando atinge o alvo")
  @ApiResponse(responseCode = "404", description = "Meta não encontrada")
  public GoalResponse contribute(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @PathVariable UUID id,
      @Valid @RequestBody ContributeGoalRequest request
  ) {
    GoalEntity goal = goalService.contribute(id, principal.userId(), request.amount());
    return RestMappers.toResponse(goal);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Remove uma meta")
  @ApiResponse(responseCode = "404", description = "Meta não encontrada")
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @PathVariable UUID id
  ) {
    goalService.delete(id, principal.userId());
    return ResponseEntity.noContent().build();
  }
}

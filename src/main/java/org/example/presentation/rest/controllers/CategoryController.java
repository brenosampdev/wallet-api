package org.example.presentation.rest.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.example.application.dtos.category.CategoryCreateDto;
import org.example.application.dtos.category.CategoryUpdateDto;
import org.example.application.services.CategoryService;
import org.example.domain.entities.CategoryEntity;
import org.example.infrastructure.security.AuthenticatedUser;
import org.example.presentation.rest.dtos.category.CategoryResponse;
import org.example.presentation.rest.dtos.category.CreateCategoryRequest;
import org.example.presentation.rest.dtos.category.UpdateCategoryRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "CRUD de categorias do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PostMapping
  @Operation(summary = "Cria uma categoria")
  @ApiResponse(responseCode = "409", description = "Categoria com mesmo título já existe")
  public ResponseEntity<CategoryResponse> create(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @Valid @RequestBody CreateCategoryRequest request
  ) {
    CategoryEntity category = categoryService.create(
        new CategoryCreateDto(principal.userId(), request.title(), request.description()));
    return ResponseEntity.created(URI.create("/api/v1/categories/" + category.getId()))
        .body(RestMappers.toResponse(category));
  }

  @GetMapping
  @Operation(summary = "Lista categorias do usuário; aceita filtro opcional por trecho do título")
  public List<CategoryResponse> list(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @RequestParam(required = false) String title
  ) {
    return categoryService.listForUser(principal.userId(), title)
        .stream().map(RestMappers::toResponse).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Busca uma categoria por id")
  @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
  public CategoryResponse get(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @PathVariable UUID id
  ) {
    return RestMappers.toResponse(categoryService.findByIdForUser(id, principal.userId()));
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Atualiza título e/ou descrição de uma categoria")
  @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
  @ApiResponse(responseCode = "409", description = "Já existe outra categoria com este título")
  public CategoryResponse update(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @PathVariable UUID id,
      @Valid @RequestBody UpdateCategoryRequest request
  ) {
    CategoryEntity category = categoryService.update(
        new CategoryUpdateDto(id, principal.userId(), request.title(), request.description()));
    return RestMappers.toResponse(category);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Remove uma categoria")
  @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
  @ApiResponse(responseCode = "409", description = "Categoria possui transações vinculadas")
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @PathVariable UUID id
  ) {
    categoryService.delete(id, principal.userId());
    return ResponseEntity.noContent().build();
  }
}

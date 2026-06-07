package org.example.presentation.rest.controllers;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import org.example.application.dtos.transaction.TransactionCreateDto;
import org.example.application.dtos.transaction.TransactionFilterDto;
import org.example.application.dtos.transaction.TransactionUpdateDto;
import org.example.application.services.TransactionService;
import org.example.domain.entities.TransactionEntity;
import org.example.domain.enums.transactions.TransactionType;
import org.example.infrastructure.security.AuthenticatedUser;
import org.example.presentation.rest.dtos.transaction.CreateTransactionRequest;
import org.example.presentation.rest.dtos.transaction.PageResponse;
import org.example.presentation.rest.dtos.transaction.TransactionResponse;
import org.example.presentation.rest.dtos.transaction.UpdateTransactionRequest;
import org.example.presentation.rest.mappers.RestMappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Transações financeiras do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping
  @Operation(summary = "Cria uma transação e atualiza o saldo do usuário")
  @ApiResponse(responseCode = "404", description = "Categoria não pertence ao usuário")
  @ApiResponse(responseCode = "409", description = "OUTPUT com saldo insuficiente")
  public ResponseEntity<TransactionResponse> create(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @Valid @RequestBody CreateTransactionRequest request
  ) {
    TransactionEntity tx = transactionService.create(new TransactionCreateDto(
        principal.userId(),
        request.type(),
        request.amount(),
        request.categoryId(),
        request.dateTime(),
        request.description(),
        request.installments()
    ));
    return ResponseEntity.created(URI.create("/api/v1/transactions/" + tx.getId()))
        .body(RestMappers.toResponse(tx));
  }

  @GetMapping
  @Operation(summary = "Lista transações paginadas com filtros opcionais")
  public PageResponse<TransactionResponse> list(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @RequestParam(required = false) TransactionType type,
      @RequestParam(required = false) UUID categoryId,
      @RequestParam(required = false) Instant from,
      @RequestParam(required = false) Instant to,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateTime"));
    Page<TransactionEntity> result = transactionService.search(
        new TransactionFilterDto(principal.userId(), type, categoryId, from, to), pageable);
    Page<TransactionResponse> mapped = result.map(RestMappers::toResponse);
    return PageResponse.from(mapped, mapped.getContent());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Busca uma transação por id")
  @ApiResponse(responseCode = "404", description = "Transação não encontrada")
  public TransactionResponse get(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @PathVariable UUID id
  ) {
    return RestMappers.toResponse(transactionService.findByIdForUser(id, principal.userId()));
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Atualiza uma transação; reverte e reaplica o efeito no saldo")
  @ApiResponse(responseCode = "404", description = "Transação ou categoria não encontrada")
  @ApiResponse(responseCode = "409", description = "Saldo insuficiente após a atualização")
  public TransactionResponse update(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @PathVariable UUID id,
      @Valid @RequestBody UpdateTransactionRequest request
  ) {
    TransactionEntity tx = transactionService.update(new TransactionUpdateDto(
        id, principal.userId(),
        request.type(), request.amount(), request.categoryId(),
        request.dateTime(), request.description(), request.installments()
    ));
    return RestMappers.toResponse(tx);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Remove uma transação e reverte seu efeito no saldo")
  @ApiResponse(responseCode = "404", description = "Transação não encontrada")
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal AuthenticatedUser principal,
      @PathVariable UUID id
  ) {
    transactionService.delete(id, principal.userId());
    return ResponseEntity.noContent().build();
  }
}

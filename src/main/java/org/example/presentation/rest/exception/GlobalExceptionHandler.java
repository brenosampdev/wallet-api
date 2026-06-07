package org.example.presentation.rest.exception;

import java.util.List;
import java.util.Map;

import org.example.application.exceptions.ConflictException;
import org.example.application.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
    return problem(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage());
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ProblemDetail handleJpaNotFound(EntityNotFoundException ex) {
    return problem(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage());
  }

  @ExceptionHandler(ConflictException.class)
  public ProblemDetail handleConflict(ConflictException ex) {
    return problem(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ProblemDetail handleIllegalState(IllegalStateException ex) {
    return problem(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
    return problem(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .map(f -> Map.of("field", f.getField(), "message", f.getDefaultMessage() == null ? "invalid" : f.getDefaultMessage()))
        .toList();
    ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Validation failed", "Os dados informados são inválidos");
    pd.setProperty("errors", fieldErrors);
    return pd;
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return problem(HttpStatus.BAD_REQUEST, "Bad request",
        "Parâmetro '" + ex.getName() + "' tem valor inválido");
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ProblemDetail handleUnreadable(HttpMessageNotReadableException ex) {
    String detail = "Corpo da requisição inválido ou malformado";
    Throwable cause = ex.getMostSpecificCause();
    if (cause != null && cause.getMessage() != null) {
      String msg = cause.getMessage();
      if (msg.contains("Cannot deserialize")) {
        int idx = msg.indexOf("Cannot deserialize");
        detail = "JSON inválido: " + msg.substring(idx, Math.min(idx + 160, msg.length()));
      } else if (msg.contains("not one of the values accepted for EnumClass")) {
        detail = "Valor de enum inválido; use os valores aceitos (case-sensitive)";
      } else if (msg.contains("Required request body is missing")) {
        detail = "Corpo da requisição é obrigatório";
      }
    }
    return problem(HttpStatus.BAD_REQUEST, "Bad request", detail);
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  public ProblemDetail handleOptimistic(OptimisticLockingFailureException ex) {
    return problem(HttpStatus.CONFLICT, "Concurrent modification",
        "Recurso foi modificado por outra requisição; tente novamente");
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
    return problem(HttpStatus.CONFLICT, "Data integrity violation",
        "Operação viola uma restrição do banco de dados");
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
    return problem(HttpStatus.UNAUTHORIZED, "Unauthorized", "Credenciais inválidas");
  }

  @ExceptionHandler(AuthenticationException.class)
  public ProblemDetail handleAuth(AuthenticationException ex) {
    return problem(HttpStatus.UNAUTHORIZED, "Unauthorized", "Autenticação necessária");
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleFallback(Exception ex) {
    return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
        "Erro interno do servidor");
  }

  private static ProblemDetail problem(HttpStatus status, String title, String detail) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
    pd.setTitle(title);
    return pd;
  }
}

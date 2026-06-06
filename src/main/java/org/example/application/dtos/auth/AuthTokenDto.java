package org.example.application.dtos.auth;

public record AuthTokenDto(String accessToken, String tokenType, long expiresIn) {
  public static AuthTokenDto bearer(String token, long expiresIn) {
    return new AuthTokenDto(token, "Bearer", expiresIn);
  }
}

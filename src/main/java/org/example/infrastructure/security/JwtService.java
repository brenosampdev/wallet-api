package org.example.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.example.domain.entities.UserEntity;
import org.example.domain.enums.users.Role;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private final JwtProperties properties;
  private final SecretKey key;

  public JwtService(JwtProperties properties) {
    this.properties = properties;
    byte[] secretBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
    if (secretBytes.length < 32) {
      throw new IllegalStateException("app.jwt.secret deve ter no mínimo 32 bytes (256 bits)");
    }
    this.key = Keys.hmacShaKeyFor(secretBytes);
  }

  public String generate(UserEntity user) {
    Instant now = Instant.now();
    Instant exp = now.plus(Duration.ofSeconds(properties.getExpirationSeconds()));
    return Jwts.builder()
        .issuer(properties.getIssuer())
        .subject(user.getId().toString())
        .claim("email", user.getEmail())
        .claim("role", user.getRole().name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(key)
        .compact();
  }

  public AuthenticatedUser parse(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(key)
        .requireIssuer(properties.getIssuer())
        .build()
        .parseSignedClaims(token)
        .getPayload();

    UUID userId = UUID.fromString(claims.getSubject());
    String email = claims.get("email", String.class);
    Role role = Role.valueOf(claims.get("role", String.class));
    return new AuthenticatedUser(userId, email, role, "");
  }

  public long getExpirationSeconds() {
    return properties.getExpirationSeconds();
  }
}

package org.example.application.services;

import org.example.application.dtos.auth.AuthTokenDto;
import org.example.application.dtos.auth.RegisterDto;
import org.example.application.exceptions.ConflictException;
import org.example.domain.entities.UserEntity;
import org.example.infrastructure.persistence.UserJpaRepository;
import org.example.infrastructure.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UserJpaRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthService(
      UserJpaRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtService jwtService
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @Transactional
  public UserEntity register(RegisterDto dto) {
    if (userRepository.existsByEmail(dto.email())) {
      throw new ConflictException("e-mail já cadastrado");
    }
    UserEntity user = new UserEntity(
        dto.name().trim(),
        dto.email().trim().toLowerCase(),
        passwordEncoder.encode(dto.password())
    );
    return userRepository.save(user);
  }

  public AuthTokenDto login(String email, String rawPassword) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email.trim().toLowerCase(), rawPassword)
    );
    UserEntity user = userRepository.findByEmail(email.trim().toLowerCase())
        .orElseThrow(() -> new IllegalStateException("usuário autenticado não encontrado"));
    String token = jwtService.generate(user);
    return AuthTokenDto.bearer(token, jwtService.getExpirationSeconds());
  }
}

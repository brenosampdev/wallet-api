package org.example.application.services;

import java.util.UUID;

import org.example.application.dtos.user.UserUpdateDto;
import org.example.application.exceptions.ResourceNotFoundException;
import org.example.domain.entities.UserEntity;
import org.example.infrastructure.persistence.UserJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserJpaRepository userRepository;

  public UserService(UserJpaRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public UserEntity getById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado"));
  }

  @Transactional
  public UserEntity update(UserUpdateDto dto) {
    UserEntity user = getById(dto.id());
    user.rename(dto.name());
    return userRepository.save(user);
  }
}

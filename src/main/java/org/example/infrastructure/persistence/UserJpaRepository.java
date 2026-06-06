package org.example.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.example.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByEmail(String email);
  boolean existsByEmail(String email);
}

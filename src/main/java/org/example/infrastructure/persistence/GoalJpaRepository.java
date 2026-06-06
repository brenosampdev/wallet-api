package org.example.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.domain.entities.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalJpaRepository extends JpaRepository<GoalEntity, UUID> {
  Optional<GoalEntity> findByIdAndUserId(UUID id, UUID userId);
  List<GoalEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
}

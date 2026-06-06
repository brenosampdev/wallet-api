package org.example.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.domain.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {
  Optional<CategoryEntity> findByIdAndUserId(UUID id, UUID userId);
  List<CategoryEntity> findAllByUserIdOrderByTitleAsc(UUID userId);
  List<CategoryEntity> findAllByUserIdAndTitleContainingIgnoreCaseOrderByTitleAsc(UUID userId, String titleFragment);
  boolean existsByUserIdAndTitleIgnoreCase(UUID userId, String title);
}

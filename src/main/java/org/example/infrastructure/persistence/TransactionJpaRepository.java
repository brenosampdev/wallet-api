package org.example.infrastructure.persistence;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.example.domain.entities.TransactionEntity;
import org.example.domain.enums.transactions.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

  Optional<TransactionEntity> findByIdAndUserId(UUID id, UUID userId);

  boolean existsByCategoryId(UUID categoryId);

  @Query("""
      SELECT t FROM TransactionEntity t
      WHERE t.userId = :userId
        AND (:type IS NULL OR t.type = :type)
        AND (:categoryId IS NULL OR t.categoryId = :categoryId)
        AND (CAST(:from AS timestamp) IS NULL OR t.dateTime >= :from)
        AND (CAST(:to AS timestamp) IS NULL OR t.dateTime <= :to)
      """)
  Page<TransactionEntity> search(
      @Param("userId") UUID userId,
      @Param("type") TransactionType type,
      @Param("categoryId") UUID categoryId,
      @Param("from") Instant from,
      @Param("to") Instant to,
      Pageable pageable
  );
}

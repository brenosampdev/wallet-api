package org.example.domain.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.example.domain.enums.transactions.TransactionType;

@Entity
@Table(
  name = "transactions",
  indexes = {
    @Index(name = "idx_transactions_user_datetime", columnList = "user_id,date_time DESC"),
    @Index(name = "idx_transactions_category", columnList = "category_id")
  }
)
public class TransactionEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "category_id", nullable = false)
  private UUID categoryId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private TransactionType type;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(name = "date_time", nullable = false)
  private Instant dateTime;

  @Column(length = 255)
  private String description;

  @Column(nullable = false)
  private Integer installments;

  @Column(name = "created_at", updatable = false, nullable = false)
  private Instant createdAt;

  protected TransactionEntity() {}

  public TransactionEntity(
      UUID userId,
      UUID categoryId,
      TransactionType type,
      BigDecimal amount,
      Instant dateTime,
      String description,
      Integer installments
  ) {
    this.userId = Objects.requireNonNull(userId, "userId obrigatório");
    this.categoryId = Objects.requireNonNull(categoryId, "categoryId obrigatório");
    this.type = Objects.requireNonNull(type, "type obrigatório");
    this.amount = Objects.requireNonNull(amount, "amount obrigatório");
    this.dateTime = (dateTime == null) ? Instant.now() : dateTime;
    this.installments = (installments == null) ? 1 : installments;
    this.description = description;
    this.createdAt = Instant.now();

    if (amount.signum() <= 0) {
      throw new IllegalArgumentException("amount deve ser > 0");
    }
    if (this.installments <= 0) {
      throw new IllegalArgumentException("installments deve ser > 0");
    }
  }

  public void update(
      TransactionType newType,
      BigDecimal newAmount,
      Instant newDateTime,
      String newDescription,
      Integer newInstallments,
      UUID newCategoryId
  ) {
    if (newType != null) this.type = newType;
    if (newAmount != null) {
      if (newAmount.signum() <= 0) throw new IllegalArgumentException("amount deve ser > 0");
      this.amount = newAmount;
    }
    if (newDateTime != null) this.dateTime = newDateTime;
    if (newDescription != null) this.description = newDescription;
    if (newInstallments != null) {
      if (newInstallments <= 0) throw new IllegalArgumentException("installments deve ser > 0");
      this.installments = newInstallments;
    }
    if (newCategoryId != null) this.categoryId = newCategoryId;
  }

  public UUID getId() { return id; }
  public UUID getUserId() { return userId; }
  public UUID getCategoryId() { return categoryId; }
  public TransactionType getType() { return type; }
  public BigDecimal getAmount() { return amount; }
  public Instant getDateTime() { return dateTime; }
  public String getDescription() { return description; }
  public Integer getInstallments() { return installments; }
  public Instant getCreatedAt() { return createdAt; }
}

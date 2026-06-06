package org.example.domain.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "goals")
public class GoalEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(nullable = false, length = 120)
  private String title;

  @Column(length = 255)
  private String description;

  @Column(name = "target_amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal targetAmount;

  @Column(name = "current_amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal currentAmount;

  @Column(nullable = false)
  private boolean completed;

  @Column(name = "created_at", updatable = false, nullable = false)
  private Instant createdAt;

  protected GoalEntity() {}

  public GoalEntity(UUID userId, String title, String description, BigDecimal targetAmount) {
    this.userId = Objects.requireNonNull(userId, "userId obrigatório");
    this.title = requireValidTitle(title);
    this.description = description;
    this.targetAmount = requireValidTargetAmount(targetAmount);
    this.currentAmount = BigDecimal.ZERO;
    this.completed = false;
    this.createdAt = Instant.now();
  }

  public void update(String newTitle, String newDescription, BigDecimal newTargetAmount) {
    if (newTitle != null) this.title = requireValidTitle(newTitle);
    if (newDescription != null) this.description = newDescription;
    if (newTargetAmount != null) {
      this.targetAmount = requireValidTargetAmount(newTargetAmount);
      refreshCompletion();
    }
  }

  public void contribute(BigDecimal amount) {
    Objects.requireNonNull(amount, "amount obrigatório");
    if (amount.signum() <= 0) {
      throw new IllegalArgumentException("amount deve ser > 0");
    }
    this.currentAmount = this.currentAmount.add(amount);
    refreshCompletion();
  }

  private void refreshCompletion() {
    this.completed = this.currentAmount.compareTo(this.targetAmount) >= 0;
  }

  private static String requireValidTitle(String title) {
    Objects.requireNonNull(title, "title obrigatório");
    String trimmed = title.trim();
    if (trimmed.isEmpty()) throw new IllegalArgumentException("title obrigatório");
    return trimmed;
  }

  private static BigDecimal requireValidTargetAmount(BigDecimal value) {
    Objects.requireNonNull(value, "targetAmount obrigatório");
    if (value.signum() <= 0) {
      throw new IllegalArgumentException("targetAmount deve ser > 0");
    }
    return value;
  }

  public UUID getId() { return id; }
  public UUID getUserId() { return userId; }
  public String getTitle() { return title; }
  public String getDescription() { return description; }
  public BigDecimal getTargetAmount() { return targetAmount; }
  public BigDecimal getCurrentAmount() { return currentAmount; }
  public boolean isCompleted() { return completed; }
  public Instant getCreatedAt() { return createdAt; }
}

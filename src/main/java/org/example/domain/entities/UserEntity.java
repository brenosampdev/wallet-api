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
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.example.domain.enums.users.Role;

@Entity
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false, length = 120)
  private String name;

  @Column(nullable = false, length = 180, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal balance;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role;

  @Column(name = "created_at", updatable = false, nullable = false)
  private Instant createdAt;

  @Version
  private Long version;

  protected UserEntity() {}

  public UserEntity(String name, String email, String passwordHash) {
    this.name = Objects.requireNonNull(name, "name obrigatório");
    this.email = Objects.requireNonNull(email, "email obrigatório");
    this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash obrigatório");
    this.balance = BigDecimal.ZERO;
    this.role = Role.USER;
    this.createdAt = Instant.now();
  }

  public void applyTransaction(TransactionEntity transaction) {
    Objects.requireNonNull(transaction, "transação obrigatória");

    BigDecimal txAmount = Objects.requireNonNull(transaction.getAmount(), "valor da transação obrigatório");
    if (txAmount.signum() <= 0) {
      throw new IllegalArgumentException("valor da transação deve ser > 0");
    }

    switch (transaction.getType()) {
      case INPUT -> this.balance = this.balance.add(txAmount);
      case OUTPUT -> {
        if (this.balance.compareTo(txAmount) < 0) {
          throw new IllegalStateException("saldo insuficiente");
        }
        this.balance = this.balance.subtract(txAmount);
      }
    }
  }

  public void revertTransaction(TransactionEntity transaction) {
    Objects.requireNonNull(transaction, "transação obrigatória");
    BigDecimal txAmount = transaction.getAmount();
    switch (transaction.getType()) {
      case INPUT -> {
        if (this.balance.compareTo(txAmount) < 0) {
          throw new IllegalStateException("não é possível reverter: saldo insuficiente");
        }
        this.balance = this.balance.subtract(txAmount);
      }
      case OUTPUT -> this.balance = this.balance.add(txAmount);
    }
  }

  public void rename(String newName) {
    if (newName == null || newName.isBlank()) {
      throw new IllegalArgumentException("nome obrigatório");
    }
    this.name = newName.trim();
  }

  public UUID getId() { return id; }
  public String getName() { return name; }
  public String getEmail() { return email; }
  public String getPasswordHash() { return passwordHash; }
  public BigDecimal getBalance() { return balance; }
  public Role getRole() { return role; }
  public Instant getCreatedAt() { return createdAt; }
  public Long getVersion() { return version; }
}

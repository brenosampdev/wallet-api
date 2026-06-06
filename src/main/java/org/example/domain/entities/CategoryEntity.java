package org.example.domain.entities;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
  name = "categories",
  uniqueConstraints = @UniqueConstraint(name = "uk_categories_user_title", columnNames = {"user_id", "title"})
)
public class CategoryEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(nullable = false, length = 80)
  private String title;

  @Column(length = 255)
  private String description;

  @Column(name = "created_at", updatable = false, nullable = false)
  private Instant createdAt;

  protected CategoryEntity() {}

  public CategoryEntity(UUID userId, String title, String description) {
    this.userId = Objects.requireNonNull(userId, "userId obrigatório");
    this.title = requireValidTitle(title);
    this.description = description;
    this.createdAt = Instant.now();
  }

  public void update(String newTitle, String newDescription) {
    if (newTitle != null) {
      this.title = requireValidTitle(newTitle);
    }
    if (newDescription != null) {
      this.description = newDescription;
    }
  }

  private static String requireValidTitle(String title) {
    Objects.requireNonNull(title, "title obrigatório");
    String trimmed = title.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("title obrigatório");
    }
    return trimmed;
  }

  public UUID getId() { return id; }
  public UUID getUserId() { return userId; }
  public String getTitle() { return title; }
  public String getDescription() { return description; }
  public Instant getCreatedAt() { return createdAt; }
}

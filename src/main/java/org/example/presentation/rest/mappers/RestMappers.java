package org.example.presentation.rest.mappers;

import org.example.domain.entities.CategoryEntity;
import org.example.domain.entities.GoalEntity;
import org.example.domain.entities.TransactionEntity;
import org.example.domain.entities.UserEntity;
import org.example.presentation.rest.dtos.category.CategoryResponse;
import org.example.presentation.rest.dtos.goal.GoalResponse;
import org.example.presentation.rest.dtos.transaction.TransactionResponse;
import org.example.presentation.rest.dtos.user.UserResponse;

public final class RestMappers {
  private RestMappers() {}

  public static UserResponse toResponse(UserEntity u) {
    return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getBalance(), u.getCreatedAt());
  }

  public static CategoryResponse toResponse(CategoryEntity c) {
    return new CategoryResponse(c.getId(), c.getTitle(), c.getDescription(), c.getCreatedAt());
  }

  public static TransactionResponse toResponse(TransactionEntity t) {
    return new TransactionResponse(
        t.getId(), t.getType(), t.getAmount(), t.getCategoryId(),
        t.getDateTime(), t.getDescription(), t.getInstallments(), t.getCreatedAt());
  }

  public static GoalResponse toResponse(GoalEntity g) {
    return new GoalResponse(
        g.getId(), g.getTitle(), g.getDescription(),
        g.getTargetAmount(), g.getCurrentAmount(), g.isCompleted(), g.getCreatedAt());
  }
}

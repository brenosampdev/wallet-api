package org.example.application.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.example.application.dtos.goal.GoalCreateDto;
import org.example.application.dtos.goal.GoalUpdateDto;
import org.example.application.exceptions.ResourceNotFoundException;
import org.example.domain.entities.GoalEntity;
import org.example.infrastructure.persistence.GoalJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoalService {

  private final GoalJpaRepository goalRepository;

  public GoalService(GoalJpaRepository goalRepository) {
    this.goalRepository = goalRepository;
  }

  @Transactional
  public GoalEntity create(GoalCreateDto dto) {
    GoalEntity goal = new GoalEntity(dto.userId(), dto.title(), dto.description(), dto.targetAmount());
    return goalRepository.save(goal);
  }

  @Transactional(readOnly = true)
  public List<GoalEntity> listForUser(UUID userId) {
    return goalRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
  }

  @Transactional(readOnly = true)
  public GoalEntity findByIdForUser(UUID id, UUID userId) {
    return goalRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("meta não encontrada"));
  }

  @Transactional
  public GoalEntity update(GoalUpdateDto dto) {
    GoalEntity goal = findByIdForUser(dto.id(), dto.userId());
    goal.update(dto.title(), dto.description(), dto.targetAmount());
    return goalRepository.save(goal);
  }

  @Transactional
  public GoalEntity contribute(UUID id, UUID userId, BigDecimal amount) {
    GoalEntity goal = findByIdForUser(id, userId);
    goal.contribute(amount);
    return goalRepository.save(goal);
  }

  @Transactional
  public void delete(UUID id, UUID userId) {
    GoalEntity goal = findByIdForUser(id, userId);
    goalRepository.delete(goal);
  }
}

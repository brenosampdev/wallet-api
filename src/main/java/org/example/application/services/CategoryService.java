package org.example.application.services;

import java.util.List;
import java.util.UUID;

import org.example.application.dtos.category.CategoryCreateDto;
import org.example.application.dtos.category.CategoryUpdateDto;
import org.example.application.exceptions.ConflictException;
import org.example.application.exceptions.ResourceNotFoundException;
import org.example.domain.entities.CategoryEntity;
import org.example.infrastructure.persistence.CategoryJpaRepository;
import org.example.infrastructure.persistence.TransactionJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

  private final CategoryJpaRepository categoryRepository;
  private final TransactionJpaRepository transactionRepository;

  public CategoryService(
      CategoryJpaRepository categoryRepository,
      TransactionJpaRepository transactionRepository
  ) {
    this.categoryRepository = categoryRepository;
    this.transactionRepository = transactionRepository;
  }

  @Transactional
  public CategoryEntity create(CategoryCreateDto dto) {
    if (categoryRepository.existsByUserIdAndTitleIgnoreCase(dto.userId(), dto.title().trim())) {
      throw new ConflictException("categoria já existe");
    }
    CategoryEntity category = new CategoryEntity(dto.userId(), dto.title(), dto.description());
    return categoryRepository.save(category);
  }

  @Transactional(readOnly = true)
  public CategoryEntity findByIdForUser(UUID id, UUID userId) {
    return categoryRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("categoria não encontrada"));
  }

  @Transactional(readOnly = true)
  public List<CategoryEntity> listForUser(UUID userId, String titleFragment) {
    if (titleFragment == null || titleFragment.isBlank()) {
      return categoryRepository.findAllByUserIdOrderByTitleAsc(userId);
    }
    return categoryRepository
        .findAllByUserIdAndTitleContainingIgnoreCaseOrderByTitleAsc(userId, titleFragment.trim());
  }

  @Transactional
  public CategoryEntity update(CategoryUpdateDto dto) {
    CategoryEntity current = findByIdForUser(dto.id(), dto.userId());

    if (dto.title() != null && !dto.title().trim().equalsIgnoreCase(current.getTitle())
        && categoryRepository.existsByUserIdAndTitleIgnoreCase(dto.userId(), dto.title().trim())) {
      throw new ConflictException("já existe outra categoria com este título");
    }

    current.update(dto.title(), dto.description());
    return categoryRepository.save(current);
  }

  @Transactional
  public void delete(UUID id, UUID userId) {
    CategoryEntity category = findByIdForUser(id, userId);
    if (transactionRepository.existsByCategoryId(category.getId())) {
      throw new ConflictException("categoria possui transações vinculadas");
    }
    categoryRepository.delete(category);
  }
}

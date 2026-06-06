package org.example.application.services;

import java.util.UUID;

import org.example.application.dtos.transaction.TransactionCreateDto;
import org.example.application.dtos.transaction.TransactionFilterDto;
import org.example.application.dtos.transaction.TransactionUpdateDto;
import org.example.application.exceptions.ResourceNotFoundException;
import org.example.domain.entities.CategoryEntity;
import org.example.domain.entities.TransactionEntity;
import org.example.domain.entities.UserEntity;
import org.example.infrastructure.persistence.CategoryJpaRepository;
import org.example.infrastructure.persistence.TransactionJpaRepository;
import org.example.infrastructure.persistence.UserJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

  private final TransactionJpaRepository transactionRepository;
  private final UserJpaRepository userRepository;
  private final CategoryJpaRepository categoryRepository;

  public TransactionService(
      TransactionJpaRepository transactionRepository,
      UserJpaRepository userRepository,
      CategoryJpaRepository categoryRepository
  ) {
    this.transactionRepository = transactionRepository;
    this.userRepository = userRepository;
    this.categoryRepository = categoryRepository;
  }

  @Transactional
  public TransactionEntity create(TransactionCreateDto dto) {
    UserEntity user = loadUser(dto.userId());
    CategoryEntity category = loadCategory(dto.categoryId(), dto.userId());

    TransactionEntity tx = new TransactionEntity(
        dto.userId(),
        category.getId(),
        dto.type(),
        dto.amount(),
        dto.dateTime(),
        dto.description(),
        dto.installments()
    );

    user.applyTransaction(tx);
    userRepository.save(user);
    return transactionRepository.save(tx);
  }

  @Transactional(readOnly = true)
  public TransactionEntity findByIdForUser(UUID id, UUID userId) {
    return transactionRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("transação não encontrada"));
  }

  @Transactional(readOnly = true)
  public Page<TransactionEntity> search(TransactionFilterDto filter, Pageable pageable) {
    return transactionRepository.search(
        filter.userId(),
        filter.type(),
        filter.categoryId(),
        filter.from(),
        filter.to(),
        pageable
    );
  }

  @Transactional
  public TransactionEntity update(TransactionUpdateDto dto) {
    TransactionEntity current = findByIdForUser(dto.id(), dto.userId());
    UserEntity user = loadUser(dto.userId());

    UUID effectiveCategoryId = current.getCategoryId();
    if (dto.categoryId() != null && !dto.categoryId().equals(current.getCategoryId())) {
      effectiveCategoryId = loadCategory(dto.categoryId(), dto.userId()).getId();
    }

    user.revertTransaction(current);
    current.update(
        dto.type(),
        dto.amount(),
        dto.dateTime(),
        dto.description(),
        dto.installments(),
        effectiveCategoryId
    );
    user.applyTransaction(current);

    userRepository.save(user);
    return transactionRepository.save(current);
  }

  @Transactional
  public void delete(UUID id, UUID userId) {
    TransactionEntity tx = findByIdForUser(id, userId);
    UserEntity user = loadUser(userId);
    user.revertTransaction(tx);
    userRepository.save(user);
    transactionRepository.delete(tx);
  }

  private UserEntity loadUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado"));
  }

  private CategoryEntity loadCategory(UUID categoryId, UUID userId) {
    return categoryRepository.findByIdAndUserId(categoryId, userId)
        .orElseThrow(() -> new ResourceNotFoundException("categoria não encontrada"));
  }
}

package org.example.presentation.rest.dtos.transaction;

import java.util.List;

import org.springframework.data.domain.Page;

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
  public static <T> PageResponse<T> from(Page<?> source, List<T> mapped) {
    return new PageResponse<>(
        mapped,
        source.getNumber(),
        source.getSize(),
        source.getTotalElements(),
        source.getTotalPages()
    );
  }
}

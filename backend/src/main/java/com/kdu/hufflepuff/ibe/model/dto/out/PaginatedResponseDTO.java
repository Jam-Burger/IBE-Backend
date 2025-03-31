package com.kdu.hufflepuff.ibe.model.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponseDTO<T> {
    private List<T> items;
    private long total;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PaginatedResponseDTO<T> of(List<T> items, long total, int currentPage, int pageSize) {
        int totalPages = (int) Math.ceil((double) total / pageSize);
        return PaginatedResponseDTO.<T>builder()
            .items(items)
            .total(total)
            .currentPage(currentPage)
            .pageSize(pageSize)
            .totalPages(totalPages)
            .hasNext(currentPage < totalPages)
            .hasPrevious(currentPage > 1)
            .build();
    }
} 
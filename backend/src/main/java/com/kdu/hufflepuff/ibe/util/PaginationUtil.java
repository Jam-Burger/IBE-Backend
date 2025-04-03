package com.kdu.hufflepuff.ibe.util;

import com.kdu.hufflepuff.ibe.model.dto.out.PaginatedResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Utility class for handling pagination operations on collections.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationUtil {

    /**
     * Default page size if not specified
     */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * Default page number if not specified
     */
    private static final int DEFAULT_PAGE = 1;

    /**
     * Paginates a list of items with the given page and page size.
     *
     * @param items    The list of items to paginate
     * @param page     The requested page number (1-based)
     * @param pageSize The number of items per page
     * @param <T>      The type of items in the list
     * @return A PaginatedResponseDTO containing the paginated items and pagination metadata
     */
    public static <T> PaginatedResponseDTO<T> paginate(List<T> items, Integer page, Integer pageSize) {
        int currentPage = page != null ? page : DEFAULT_PAGE;
        int size = pageSize != null ? pageSize : DEFAULT_PAGE_SIZE;

        currentPage = Math.max(1, currentPage);
        size = Math.max(1, size);

        int startIndex = (currentPage - 1) * size;
        int endIndex = Math.min(startIndex + size, items.size());

        if (startIndex >= items.size()) {
            return PaginatedResponseDTO.of(
                List.of(),
                items.size(),
                currentPage,
                size
            );
        }

        List<T> paginatedItems = items.subList(startIndex, endIndex);
        return PaginatedResponseDTO.of(
            paginatedItems,
            items.size(),
            currentPage,
            size
        );
    }

    /**
     * Paginates a list of items with default pagination values.
     *
     * @param items The list of items to paginate
     * @param <T>   The type of items in the list
     * @return A PaginatedResponseDTO containing the paginated items and pagination metadata
     */
    public static <T> PaginatedResponseDTO<T> paginate(List<T> items) {
        return paginate(items, null, null);
    }
} 
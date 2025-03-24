package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.in.CreateSpecialDiscountRequest;
import com.kdu.hufflepuff.ibe.model.entity.SpecialDiscount;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialDiscountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecialDiscountControllerTest {

    @Mock
    private SpecialDiscountService specialDiscountService;

    @InjectMocks
    private SpecialDiscountController specialDiscountController;

    private Long tenantId;
    private Long propertyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<SpecialDiscount> mockDiscounts;
    private SpecialDiscount mockDiscount;
    private CreateSpecialDiscountRequest mockRequest;

    @BeforeEach
    void setUp() {
        tenantId = 1L;
        propertyId = 2L;
        startDate = LocalDate.now();
        endDate = LocalDate.now().plusDays(7);

        mockDiscount = SpecialDiscount.builder()
            .propertyId(propertyId)
            .discountDate(startDate)
            .discountPercentage(10.0)
            .build();

        mockDiscounts = List.of(mockDiscount);

        mockRequest = new CreateSpecialDiscountRequest();
        mockRequest.setDiscountDate(startDate);
        mockRequest.setDiscountPercentage(10.0);
    }

    @Test
    void getSpecialDiscounts_ShouldReturnDiscounts() {
        // Arrange
        when(specialDiscountService.getSpecialDiscounts(anyLong(), anyLong(), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(mockDiscounts);

        // Act
        ResponseEntity<ApiResponse<List<SpecialDiscount>>> responseEntity =
            specialDiscountController.getSpecialDiscounts(tenantId, propertyId, startDate, endDate);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse<List<SpecialDiscount>> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("Special discounts retrieved successfully", response.getMessage());
        assertEquals(mockDiscounts, response.getData());

        verify(specialDiscountService).getSpecialDiscounts(tenantId, propertyId, startDate, endDate);
    }

    @Test
    void createSpecialDiscount_ShouldCreateAndReturnDiscount() {
        // Arrange
        when(specialDiscountService.createSpecialDiscount(anyLong(), anyLong(), any(CreateSpecialDiscountRequest.class)))
            .thenReturn(mockDiscount);

        // Act
        ResponseEntity<ApiResponse<SpecialDiscount>> responseEntity =
            specialDiscountController.createSpecialDiscount(tenantId, propertyId, mockRequest);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ApiResponse<SpecialDiscount> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("Special discount created successfully", response.getMessage());
        assertEquals(mockDiscount, response.getData());

        verify(specialDiscountService).createSpecialDiscount(tenantId, propertyId, mockRequest);
    }
} 
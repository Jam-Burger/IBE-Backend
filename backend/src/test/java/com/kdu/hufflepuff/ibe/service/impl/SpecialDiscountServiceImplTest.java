package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.in.CreateSpecialDiscountRequest;
import com.kdu.hufflepuff.ibe.model.entity.SpecialDiscount;
import com.kdu.hufflepuff.ibe.repository.jpa.SpecialDiscountsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecialDiscountServiceImplTest {

    @Mock
    private SpecialDiscountsRepository specialDiscountsRepository;

    @InjectMocks
    private SpecialDiscountServiceImpl specialDiscountService;

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
    void getSpecialDiscounts_ShouldReturnDiscountsFromRepository() {
        // Arrange
        when(specialDiscountsRepository.findAllByPropertyIdAndDiscountDateBetween(propertyId, startDate, endDate))
                .thenReturn(mockDiscounts);

        // Act
        List<SpecialDiscount> result = specialDiscountService.getSpecialDiscounts(tenantId, propertyId, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(mockDiscounts, result);
        verify(specialDiscountsRepository).findAllByPropertyIdAndDiscountDateBetween(propertyId, startDate, endDate);
    }

    @Test
    void createSpecialDiscount_ShouldCreateAndReturnDiscount() {
        // Arrange
        when(specialDiscountsRepository.save(any(SpecialDiscount.class))).thenReturn(mockDiscount);

        // Act
        SpecialDiscount result = specialDiscountService.createSpecialDiscount(tenantId, propertyId, mockRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockDiscount, result);
        verify(specialDiscountsRepository).save(any(SpecialDiscount.class));
    }
} 
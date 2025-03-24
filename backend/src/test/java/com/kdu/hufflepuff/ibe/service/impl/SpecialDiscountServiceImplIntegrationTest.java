package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.bootloader.IbeApplication;
import com.kdu.hufflepuff.ibe.model.dto.in.CreateSpecialDiscountRequest;
import com.kdu.hufflepuff.ibe.model.entity.SpecialDiscount;
import com.kdu.hufflepuff.ibe.repository.jpa.SpecialDiscountsRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialDiscountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = IbeApplication.class)
@ActiveProfiles("test")
@Transactional
class SpecialDiscountServiceImplIntegrationTest {

    @Autowired
    private SpecialDiscountService specialDiscountService;

    @Autowired
    private SpecialDiscountsRepository specialDiscountsRepository;

    private Long tenantId;
    private Long propertyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private CreateSpecialDiscountRequest request;

    @BeforeEach
    void setUp() {
        tenantId = 1L;
        propertyId = 2L;
        startDate = LocalDate.now();
        endDate = LocalDate.now().plusDays(7);

        // Clear any existing data
        specialDiscountsRepository.deleteAll();

        // Create some test data
        SpecialDiscount discount1 = SpecialDiscount.builder()
            .propertyId(propertyId)
            .discountDate(startDate)
            .discountPercentage(10.0)
            .build();

        SpecialDiscount discount2 = SpecialDiscount.builder()
            .propertyId(propertyId)
            .discountDate(startDate.plusDays(1))
            .discountPercentage(15.0)
            .build();

        specialDiscountsRepository.saveAll(List.of(discount1, discount2));

        request = new CreateSpecialDiscountRequest();
        request.setDiscountDate(startDate.plusDays(2));
        request.setDiscountPercentage(20.0);
    }

    @AfterEach
    void tearDown() {
        specialDiscountsRepository.deleteAll();
    }

    @Test
    void getSpecialDiscounts_ShouldReturnAllDiscountsInDateRange() {
        // Act
        List<SpecialDiscount> result = specialDiscountService.getSpecialDiscounts(tenantId, propertyId, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify properties match
        assertTrue(result.stream().allMatch(discount -> discount.getPropertyId().equals(propertyId)));
        assertTrue(result.stream().anyMatch(discount -> discount.getDiscountPercentage().equals(10.0)));
        assertTrue(result.stream().anyMatch(discount -> discount.getDiscountPercentage().equals(15.0)));
    }

    @Test
    void createSpecialDiscount_ShouldCreateNewDiscount() {
        // Act
        SpecialDiscount result = specialDiscountService.createSpecialDiscount(tenantId, propertyId, request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(propertyId, result.getPropertyId());
        assertEquals(request.getDiscountDate(), result.getDiscountDate());
        assertEquals(request.getDiscountPercentage(), result.getDiscountPercentage());

        // Verify it was persisted to the repository
        List<SpecialDiscount> allDiscounts = specialDiscountsRepository.findAllByPropertyIdAndDiscountDateBetween(
            propertyId, startDate, endDate);
        assertEquals(3, allDiscounts.size());
        assertTrue(allDiscounts.stream().anyMatch(discount ->
            discount.getDiscountDate().equals(request.getDiscountDate()) &&
                discount.getDiscountPercentage().equals(request.getDiscountPercentage())));
    }
} 
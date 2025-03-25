package com.kdu.hufflepuff.ibe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdu.hufflepuff.ibe.bootloader.IbeApplication;
import com.kdu.hufflepuff.ibe.model.dto.in.CreateSpecialDiscountRequest;
import com.kdu.hufflepuff.ibe.model.entity.SpecialDiscount;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialDiscountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = IbeApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SpecialDiscountControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @MockitoBean
    private SpecialDiscountService specialDiscountService;

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
            .id(1L)
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
    void getSpecialDiscounts_ShouldReturnDiscountList() throws Exception {
        // Arrange
        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE);

        when(specialDiscountService.getSpecialDiscounts(eq(tenantId), eq(propertyId), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(mockDiscounts);

        // Act & Assert
        mockMvc.perform(get("/api/v1/{tenantId}/{propertyId}/special-discounts", tenantId, propertyId)
                .param("startDate", startDateStr)
                .param("endDate", endDateStr)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value("OK"))
            .andExpect(jsonPath("$.message").value("Special discounts retrieved successfully"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").value(mockDiscount.getId()))
            .andExpect(jsonPath("$.data[0].propertyId").value(mockDiscount.getPropertyId()))
            .andExpect(jsonPath("$.data[0].discountPercentage").value(mockDiscount.getDiscountPercentage()));
    }

    @Test
    void createSpecialDiscount_ShouldCreateAndReturnDiscount() throws Exception {
        // Arrange
        when(specialDiscountService.createSpecialDiscount(anyLong(), anyLong(), any(CreateSpecialDiscountRequest.class)))
            .thenReturn(mockDiscount);

        // Act & Assert
        mockMvc.perform(post("/api/v1/{tenantId}/{propertyId}/special-discounts", tenantId, propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statusCode").value("CREATED"))
            .andExpect(jsonPath("$.message").value("Special discount created successfully"))
            .andExpect(jsonPath("$.data.id").value(mockDiscount.getId()))
            .andExpect(jsonPath("$.data.propertyId").value(mockDiscount.getPropertyId()))
            .andExpect(jsonPath("$.data.discountPercentage").value(mockDiscount.getDiscountPercentage()));
    }
} 
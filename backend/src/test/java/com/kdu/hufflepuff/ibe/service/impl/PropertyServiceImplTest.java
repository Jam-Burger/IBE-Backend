package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDTO;
import com.kdu.hufflepuff.ibe.model.graphql.Property;
import com.kdu.hufflepuff.ibe.repository.jpa.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.graphql.client.GraphQlClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

    private static final Long TENANT_ID = 1L;
    @Mock
    private GraphQlClient graphQlClient;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private GraphQlClient.RequestSpec requestSpec;
    @Mock
    private GraphQlClient.RetrieveSpec retrieveSpec;
    private PropertyServiceImpl propertyService;

    @BeforeEach
    void setUp() {
        propertyService = new PropertyServiceImpl(graphQlClient, modelMapper, propertyRepository);
    }

    @Test
    void getProperties_ShouldReturnEmptyList_WhenNoDataExists() {
        // Prepare
        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable("tenantId", TENANT_ID)).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
        when(retrieveSpec.toEntityList(Property.class)).thenReturn(Mono.empty());

        // Act
        List<PropertyDTO> result = propertyService.getProperties(TENANT_ID);

        // Assert
        assertThat(result).isEmpty();
        verify(modelMapper, never()).map(any(), any());
    }
} 
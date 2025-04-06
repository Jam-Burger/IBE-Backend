package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.ResourceNotFoundException;
import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDetailsDTO;
import com.kdu.hufflepuff.ibe.model.entity.PropertyExtension;
import com.kdu.hufflepuff.ibe.model.graphql.Property;
import com.kdu.hufflepuff.ibe.repository.jpa.PropertyRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.PropertyService;
import com.kdu.hufflepuff.ibe.util.GraphQLQueries;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {
    private final GraphQlClient graphQlClient;
    private final ModelMapper modelMapper;
    private final PropertyRepository propertyExtensionRepository;

    @Override
    public List<PropertyDTO> getProperties(Long tenantId) {
        List<Property> properties = graphQlClient.document(GraphQLQueries.GET_PROPERTIES_BY_TENANT)
            .variable("tenantId", tenantId)
            .retrieve("listProperties")
            .toEntityList(Property.class)
            .block();

        if (properties == null) {
            return List.of();
        }

        return properties.stream()
            .map(property -> modelMapper.map(property, PropertyDTO.class))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyDetailsDTO getPropertyDetails(Long tenantId, Long propertyId) {
        Property property = graphQlClient.document(GraphQLQueries.GET_PROPERTY_BY_ID)
            .variable("propertyId", propertyId)
            .variable("tenantId", tenantId)
            .retrieve("getProperty")
            .toEntity(Property.class)
            .block();

        if (property == null) {
            throw new ResourceNotFoundException("Property not found: " + propertyId);
        }
        PropertyDetailsDTO detailsDTO = modelMapper.map(property, PropertyDetailsDTO.class);

        Optional<PropertyExtension> extension = propertyExtensionRepository.findById(propertyId);
        extension.ifPresent(ext -> {
            detailsDTO.setAvailability(ext.getAvailability());
            detailsDTO.setCountry(ext.getCountry());
            detailsDTO.setSurcharge(ext.getSurcharge());
            detailsDTO.setFees(ext.getFees());
            detailsDTO.setTermsAndConditions(ext.getTermsAndConditions());
        });

        return detailsDTO;
    }
}

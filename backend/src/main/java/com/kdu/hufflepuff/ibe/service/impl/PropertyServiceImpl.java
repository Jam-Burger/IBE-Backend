package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDTO;
import com.kdu.hufflepuff.ibe.model.graphql.Property;
import com.kdu.hufflepuff.ibe.service.interfaces.PropertyService;
import com.kdu.hufflepuff.ibe.util.GraphQLQueries;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {
    private final GraphQlClient graphQlClient;
    private final ModelMapper modelMapper;

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
}

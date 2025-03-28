package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDTO;
import com.kdu.hufflepuff.ibe.model.graphql.Property;
import com.kdu.hufflepuff.ibe.service.interfaces.PropertyService;
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
        String query = """
                query MyQuery ($tenantId: Int!) {
                  listProperties(where: {tenant: {tenant_id: {equals: $tenantId}}}) {
                    property_name
                    property_id
                  }
                }
            """;

        List<Property> properties = graphQlClient.document(query)
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

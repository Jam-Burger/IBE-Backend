package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDTO;
import com.kdu.hufflepuff.ibe.model.graphql.Property;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Lazy;
import org.modelmapper.ModelMapper;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PropertyService {
    private final GraphQlClient graphQlClient;
    private final ModelMapper modelMapper;
    public List<PropertyDTO> getProperties() {
        String query = """
            query MyQuery ($tenantId: Int!) {
              listProperties(where: {tenant: {tenant_id: {equals: $tenantId}}}) {
                property_name
                property_id
              }
            }
        """;

        List<Property> properties=graphQlClient.document(query)
                .variable("tenantId", 1)
                .retrieve("listProperties")
                .toEntityList(Property.class)
                .block();


        return properties.stream()
                .map(property -> modelMapper.map(property, PropertyDTO.class))
                .collect(Collectors.toList());
    }
}

package com.kdu.hufflepuff.ibe.model.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    @JsonProperty("tenant_id")
    private Long tenantId;
    
    @JsonProperty("tenant_name")
    private String tenantName;
    
    private List<Property> properties;
} 
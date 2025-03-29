package com.kdu.hufflepuff.ibe.model.dynamodb;

import jakarta.validation.constraints.Size;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
public class Banner {
    private boolean enabled;

    @Size(max = 1024, message = "Image URL cannot exceed 1024 characters")
    private String imageUrl;

    @DynamoDbAttribute("Enabled")
    public boolean isEnabled() {
        return enabled;
    }

    @DynamoDbAttribute("ImageUrl")
    public String getImageUrl() {
        return imageUrl;
    }
}
package com.kdu.hufflepuff.ibe.model.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserWithTokenResponseDTO {
    private String name;
    private String email;
    private String token;
}

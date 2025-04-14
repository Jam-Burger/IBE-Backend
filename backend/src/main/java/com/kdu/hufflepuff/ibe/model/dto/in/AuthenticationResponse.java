package com.kdu.hufflepuff.ibe.model.dto.in;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String userType;
    private Long userId;
    private String name;  // Staff or admin name
}

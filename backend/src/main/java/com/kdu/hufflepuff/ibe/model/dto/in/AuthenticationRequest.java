package com.kdu.hufflepuff.ibe.model.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
    private String username;  // Can be either staff name/email or admin name/email
    private String password;
    private String userType;  // "ADMIN" or "STAFF"

}


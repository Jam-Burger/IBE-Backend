package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.entity.Admin;

public interface AdminService {
    Admin findByEmail(String email);

    boolean existsByEmail(String email);

    boolean isOfProperty(String email, Long propertyId);
}
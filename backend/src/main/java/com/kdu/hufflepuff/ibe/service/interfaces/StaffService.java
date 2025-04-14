package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.entity.Staff;

public interface StaffService {
    Staff findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsById(Long id);
}

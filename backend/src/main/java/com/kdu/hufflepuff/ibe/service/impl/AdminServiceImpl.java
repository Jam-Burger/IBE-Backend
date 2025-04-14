package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.ResourceNotFoundException;
import com.kdu.hufflepuff.ibe.model.entity.Admin;
import com.kdu.hufflepuff.ibe.repository.jpa.AdminRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    @Override
    public Admin findByEmail(String email) {
        return adminRepository.findByAdminEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Admin not found with email: " + email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return adminRepository.existsByAdminEmail(email);
    }
}

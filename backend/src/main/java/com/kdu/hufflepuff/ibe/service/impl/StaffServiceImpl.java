package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.ResourceNotFoundException;
import com.kdu.hufflepuff.ibe.model.entity.Staff;
import com.kdu.hufflepuff.ibe.repository.jpa.StaffRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;

    @Override
    public Staff findByEmail(String email) {
        return staffRepository.findByStaffEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found with email: " + email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return staffRepository.existsByStaffEmail(email);
    }

    @Override
    public boolean existsById(Long id) {
        return staffRepository.existsById(id);
    }
}

package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.mapper.GuestExtensionMapper;
import com.kdu.hufflepuff.ibe.model.entity.GuestExtension;
import com.kdu.hufflepuff.ibe.repository.jpa.GuestExtensionRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.GuestExtensionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestExtensionServiceImpl implements GuestExtensionService {
    private final GuestExtensionRepository guestExtensionRepository;

    @Override
    @Transactional
    public GuestExtension createGuestExtension(Map<String, String> formData) {
        GuestExtension guestExtension = GuestExtensionMapper.fromFormDataWithValidation(formData);
        return guestExtensionRepository.save(guestExtension);
    }

    @Override
    public GuestExtension findByEmail(String email) {
        return guestExtensionRepository.findByTravelerEmail(email).orElse(null);
    }
} 
package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.entity.GuestExtension;

import java.util.Map;

public interface GuestExtensionService {
    /**
     * Creates and saves a guest extension from form data.
     *
     * @param formData Map containing guest form data
     * @return The saved GuestExtension entity
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    GuestExtension createGuestExtension(Map<String, String> formData);
} 
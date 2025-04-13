package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.entity.GuestExtension;
import com.kdu.hufflepuff.ibe.model.graphql.Guest;

import java.util.Map;

public interface GuestService {
    Guest createGuest(String fullName);

    Guest createGuestWithId(String fullName, Long guestId);

    Guest findGuestById(Long guestId);

    GuestExtension findByEmail(String email);

    GuestExtension createGuestExtension(Map<String, String> formData);

    GuestExtension createGuestExtensionWithId(Map<String, String> formData, Long guestId);
} 
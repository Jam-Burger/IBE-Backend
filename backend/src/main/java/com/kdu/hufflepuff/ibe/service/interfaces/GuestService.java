package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.graphql.Guest;

public interface GuestService {
    Guest createGuestWithId(String fullName, Long guestId);

    Guest findGuestById(Long guestId);
} 
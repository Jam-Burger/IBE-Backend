package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.BookingExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingExtensionRepository extends JpaRepository<BookingExtension, Long> {
    BookingExtension findByBookingId(Long bookingId);
}
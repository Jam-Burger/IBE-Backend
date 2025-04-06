package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.BookingExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingExtensionRepository extends JpaRepository<BookingExtension, Long> {
    Optional<BookingExtension> findByTransactionId(String transactionId);
} 
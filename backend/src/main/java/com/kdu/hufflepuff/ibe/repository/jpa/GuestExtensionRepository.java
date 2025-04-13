package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.GuestExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestExtensionRepository extends JpaRepository<GuestExtension, Long> {
    Optional<GuestExtension> findByBillingEmail(String billingEmail);

    Optional<GuestExtension> findByGuestId(Long guestId);
}
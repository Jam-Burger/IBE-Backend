package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByAdminEmail(String adminEmail);

    boolean existsByAdminEmail(String adminEmail);

    boolean existsByAdminEmailAndPropertyId(String adminEmail, Long propertyId);
}

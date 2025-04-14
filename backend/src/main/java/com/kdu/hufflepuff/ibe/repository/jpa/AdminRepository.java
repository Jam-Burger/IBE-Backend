package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAdminEmail(String adminEmail);

    Optional<Admin> findByAdminEmailAndAdminPassword(String adminEmail, String adminPassword);

    Optional<Admin> findByAdminName(String adminName);

    Optional<Admin> findByAdminNameAndAdminPassword(String adminName, String adminPassword);

    Optional<Admin> findByAdminEmailOrAdminName(String adminEmail, String adminName);
}

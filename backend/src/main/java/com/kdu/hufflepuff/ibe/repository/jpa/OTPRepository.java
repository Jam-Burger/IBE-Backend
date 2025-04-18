package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.OTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTPEntity, Long> {
    Optional<OTPEntity> findByEmail(String email);

    OTPEntity getOTPEntityByEmailAndOtpNumber(String email, String otpNumber);

    void deleteByOtpNumber(String otpNumber);

}

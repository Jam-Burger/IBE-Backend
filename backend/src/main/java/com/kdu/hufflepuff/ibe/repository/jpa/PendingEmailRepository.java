package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.PendingEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PendingEmailRepository extends JpaRepository<PendingEmail, Long> {
    List<PendingEmail> findBySentFalseAndSendAfterBefore(LocalDateTime time);
}


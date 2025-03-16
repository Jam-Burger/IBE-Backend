package com.kdu.hufflepuff.ibe.repository;

import com.kdu.hufflepuff.ibe.model.entity.HotelTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HotelTranslationRepository extends JpaRepository<HotelTranslation, UUID> {
}


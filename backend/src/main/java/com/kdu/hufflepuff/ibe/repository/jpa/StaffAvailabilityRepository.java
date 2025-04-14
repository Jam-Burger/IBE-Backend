package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.StaffAvailability;
import com.kdu.hufflepuff.ibe.model.entity.StaffAvailability.StaffAvailabilityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StaffAvailabilityRepository extends JpaRepository<StaffAvailability, StaffAvailabilityId> {
    List<StaffAvailability> findByIdStaffId(Long staffId);

    StaffAvailability findById_StaffIdAndId_Date(Long staffId, LocalDate date);
}

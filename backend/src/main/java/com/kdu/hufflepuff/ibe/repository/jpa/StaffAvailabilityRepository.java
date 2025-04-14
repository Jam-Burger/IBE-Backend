package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.StaffAvailability;
import com.kdu.hufflepuff.ibe.model.entity.StaffAvailability.StaffAvailabilityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StaffAvailabilityRepository extends JpaRepository<StaffAvailability, StaffAvailabilityId> {

    List<StaffAvailability> findByIdStaffId(Long staffId);

    List<StaffAvailability> findByIdStaffIdAndIdDateBetween(Long staffId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT sa FROM StaffAvailability sa WHERE sa.id.staffId = :staffId AND sa.id.date = :date")
    StaffAvailability findByStaffIdAndDate(@Param("staffId") Long staffId, @Param("date") LocalDate date);
}

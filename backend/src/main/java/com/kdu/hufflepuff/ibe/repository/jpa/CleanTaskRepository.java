package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.CleanTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CleanTaskRepository extends JpaRepository<CleanTask, Long> {
    List<CleanTask> findByDateAndStaff_PreferredShift_PropertyId(LocalDate date, Long staffPreferredShiftPropertyId);
}

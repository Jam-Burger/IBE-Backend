package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByStaffEmail(String staffEmail);

    Optional<Staff> findByStaffEmailAndStaffPassword(String staffEmail, String staffPassword);

    Optional<Staff> findByStaffName(String staffName);

    Optional<Staff> findByStaffNameAndStaffPassword(String staffName, String staffPassword);

    Optional<Staff> findByStaffEmailOrStaffName(String staffEmail, String staffName);
}

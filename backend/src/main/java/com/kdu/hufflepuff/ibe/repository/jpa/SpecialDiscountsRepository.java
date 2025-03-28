package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.SpecialOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpecialDiscountsRepository extends JpaRepository<SpecialOffer, Long> {
    @Query(nativeQuery = true, value = """
        SELECT * FROM special_discount 
        WHERE property_id = :propertyId 
        AND start_date <= :endDate 
        AND end_date >= :startDate
        """)
    List<SpecialOffer> findAllByPropertyIdAndDateRange(
        @Param("propertyId") Long propertyId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}


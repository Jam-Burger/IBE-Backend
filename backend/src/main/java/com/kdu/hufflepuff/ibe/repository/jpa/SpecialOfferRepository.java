package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.SpecialOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpecialOfferRepository extends JpaRepository<SpecialOffer, Long> {
    @Query(nativeQuery = true, value = """
        SELECT * FROM special_offer
        WHERE property_id = :propertyId
        AND start_date <= :startDate
        AND end_date >= :endDate
        """)
    List<SpecialOffer> findAllByPropertyIdAndDateRange(
        @Param("propertyId") Long propertyId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query(nativeQuery = true, value = """
        SELECT * FROM special_offer
        WHERE property_id = :propertyId
        AND start_date >= :startDate
        AND end_date < :endDate
        AND promo_code IS NULL
        """)
    List<SpecialOffer> findAllByPropertyIdAndDateRangeWithNoPromoCode(Long propertyId, LocalDate startDate, LocalDate endDate);

    SpecialOffer findByPropertyIdAndPromoCode(Long propertyId, String promoCode);
}
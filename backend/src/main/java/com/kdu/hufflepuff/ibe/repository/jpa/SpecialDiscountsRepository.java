package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.SpecialDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpecialDiscountsRepository extends JpaRepository<SpecialDiscount, Long> {
    List<SpecialDiscount> findAllByPropertyIdAndDiscountDateBetween(Long propertyId, LocalDate discountDateAfter, LocalDate discountDateBefore);
}
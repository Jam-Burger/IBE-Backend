package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "reviews")
@Getter
@Setter
@Builder
public class Review extends BaseEntity {

    @Column(name = "booking_id", nullable = false, unique = true)
    private Long bookingId;

    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;


}
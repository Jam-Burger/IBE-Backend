package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Table(name = "reviews")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {
    @Column(name = "booking_id", nullable = false, unique = true)
    private Long bookingId;

    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff_availability")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffAvailability {
    @EmbeddedId
    private StaffAvailabilityId id;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    @Data
    @Embeddable
    private static class StaffAvailabilityId {
        @Column(name = "staff_id", nullable = false)
        private Long staffId;

        @Column(name = "date", nullable = false)
        private LocalDate date;
    }
}
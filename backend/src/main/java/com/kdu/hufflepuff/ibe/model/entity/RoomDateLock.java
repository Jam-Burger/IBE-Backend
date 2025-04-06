package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_date_lock")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoomDateLock {
    @EmbeddedId
    private RoomDateLockId id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "version", nullable = false)
    @Version
    private Long version;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomDateLockId implements Serializable {
        @Column(name = "room_id")
        private Long roomId;

        @Column(name = "start_date")
        private LocalDate startDate;

        @Column(name = "end_date")
        private LocalDate endDate;
    }
} 
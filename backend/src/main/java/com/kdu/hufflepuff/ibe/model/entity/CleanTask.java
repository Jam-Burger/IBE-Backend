package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "clean_task")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CleanTask extends BaseEntity {
    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @ManyToOne
    @JoinColumn(name = "task_type_id", nullable = false)
    private CleanTaskType taskType;
}
package com.kdu.hufflepuff.ibe.model.entity;

import com.kdu.hufflepuff.ibe.util.DurationConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Duration;

@Entity
@Table(name = "clean_task_type")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CleanTaskType extends BaseEntity {
    @Column(name = "type_name", nullable = false, length = 100)
    private String typeName;

    @Column(name = "required_time", nullable = false)
    @Convert(converter = DurationConverter.class)
    private Duration requiredTime;
}
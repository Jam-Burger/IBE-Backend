package com.kdu.hufflepuff.ibe.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "staff")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Staff extends BaseEntity {
    @Column(name = "staff_name", nullable = false, length = 100)
    private String staffName;

    @Column(name = "staff_email", nullable = false, unique = true, length = 100)
    private String staffEmail;

    @JsonIgnore
    @Column(name = "staff_password", nullable = false, length = 100)
    private String staffPassword;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "is_permanent_staff", nullable = false)
    private boolean isPermanentStaff;

    @ManyToOne
    @JoinColumn(name = "preferred_shift_id")
    private Shift preferredShift;
}
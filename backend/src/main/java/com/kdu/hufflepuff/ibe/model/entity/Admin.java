package com.kdu.hufflepuff.ibe.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "admin")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends BaseEntity {
    @Column(name = "admin_name", nullable = false, length = 100)
    private String adminName;

    @Column(name = "admin_email", nullable = false, unique = true, length = 100)
    private String adminEmail;

    @Column(name = "admin_password", nullable = false, length = 100)
    private String adminPassword;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "property_id", nullable = false)
    private Long propertyId;
}


package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Student {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private String email;
    private int age;
}


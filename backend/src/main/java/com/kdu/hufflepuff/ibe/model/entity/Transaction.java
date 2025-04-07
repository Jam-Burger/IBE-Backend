package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends BaseEntity {
    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
} 
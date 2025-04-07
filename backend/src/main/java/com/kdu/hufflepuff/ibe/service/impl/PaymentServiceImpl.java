package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.PaymentException;
import com.kdu.hufflepuff.ibe.model.dto.in.PaymentDTO;
import com.kdu.hufflepuff.ibe.model.entity.Transaction;
import com.kdu.hufflepuff.ibe.repository.jpa.TransactionRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final long MAX_AMOUNT = 1_000_000_00; // $1,000,000.00 in cents
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Transaction processPayment(PaymentDTO payment, Double amount) {
        validatePayment(payment, amount);

        String transactionId = UUID.randomUUID().toString();

        Transaction transaction = Transaction.builder()
            .transactionId(transactionId)
            .amount(amount)
            .status("COMPLETED")
            .timestamp(LocalDateTime.now())
            .build();

        return transactionRepository.save(transaction);
    }

    private void validatePayment(PaymentDTO payment, Double amount) {
        if (amount <= 0) {
            throw PaymentException.invalidPaymentInfo("Payment amount must be positive");
        }

        if (amount > MAX_AMOUNT) {
            throw PaymentException.invalidPaymentInfo("Payment amount exceeds maximum allowed");
        }

        YearMonth cardExpiry = YearMonth.of(2000 + Integer.parseInt(payment.getExpYear()),
            Integer.parseInt(payment.getExpMonth()));
        YearMonth now = YearMonth.from(LocalDate.now());

        if (cardExpiry.isBefore(now)) {
            throw PaymentException.invalidPaymentInfo("Card has expired");
        }

        // Simulate random payment failures (1% chance)
        if (Math.random() < 0.01) {
            throw PaymentException.paymentFailed("Payment declined by issuer");
        }
    }
} 
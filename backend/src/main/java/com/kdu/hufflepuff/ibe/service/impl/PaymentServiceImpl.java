package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.PaymentException;
import com.kdu.hufflepuff.ibe.model.dto.in.PaymentDTO;
import com.kdu.hufflepuff.ibe.service.interfaces.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final long MAX_AMOUNT = 1_000_000_00; // $1,000,000.00 in cents
    private final Map<String, PaymentRecord> transactions = new HashMap<>();

    @Override
    public String processPayment(PaymentDTO payment, long amount) {
        validatePayment(payment, amount);

        String transactionId = UUID.randomUUID().toString();

        transactions.put(transactionId, new PaymentRecord(amount, System.currentTimeMillis()));

        log.info("Processing payment of ${}.{} with card ending in {}",
            amount / 100, String.format("%02d", amount % 100),
            payment.getCardName());

        return transactionId;
    }

    @Override
    public boolean refundPayment(String transactionId) {
        PaymentRecord record = transactions.get(transactionId);
        if (record == null) {
            log.warn("Attempted to refund non-existent transaction: {}", transactionId);
            return false;
        }

        long thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000;
        if (System.currentTimeMillis() - record.timestamp > thirtyDaysInMillis) {
            log.warn("Attempted to refund transaction older than 30 days: {}", transactionId);
            return false;
        }

        transactions.remove(transactionId);
        log.info("Refunded ${}.{} for transaction {}",
            record.amount / 100, String.format("%02d", record.amount % 100),
            transactionId);

        return true;
    }

    private void validatePayment(PaymentDTO payment, long amount) {
        if (amount <= 0) {
            throw new PaymentException("Payment amount must be positive");
        }
        if (amount > MAX_AMOUNT) {
            throw new PaymentException("Payment amount exceeds maximum allowed");
        }

        YearMonth cardExpiry = YearMonth.of(2000 + Integer.parseInt(payment.getExpYear()),
            Integer.parseInt(payment.getExpMonth()));
        YearMonth now = YearMonth.from(LocalDate.now());

        if (cardExpiry.isBefore(now)) {
            throw new PaymentException("Card has expired");
        }

        // Simulate random payment failures (1% chance)
        if (Math.random() < 0.01) {
            throw new PaymentException("Payment declined by issuer");
        }
    }

    private record PaymentRecord(long amount, long timestamp) {
    }
} 
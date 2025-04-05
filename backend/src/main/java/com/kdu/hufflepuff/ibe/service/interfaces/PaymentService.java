package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.in.PaymentDTO;

public interface PaymentService {
    /**
     * Process a payment for a booking
     *
     * @param payment the payment details
     * @param amount  the amount to charge in cents
     * @return the payment transaction ID
     */
    String processPayment(PaymentDTO payment, long amount);

    /**
     * Refund a payment
     *
     * @param transactionId the transaction ID to refund
     * @return true if refund was successful
     */
    boolean refundPayment(String transactionId);
} 
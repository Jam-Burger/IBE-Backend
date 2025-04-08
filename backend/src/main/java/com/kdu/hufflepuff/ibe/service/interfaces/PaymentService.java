package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.in.PaymentDTO;
import com.kdu.hufflepuff.ibe.model.entity.Transaction;

public interface PaymentService {
    /**
     * Process a payment for a booking
     *
     * @param payment the payment details
     * @param amount  the amount to charge in cents
     * @return the payment transaction ID
     */
    Transaction processPayment(PaymentDTO payment, Double amount);

    Double calculateDueNowAmount(Double totalAmount);
}
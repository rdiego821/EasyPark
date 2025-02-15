package com.invoicepayment.payment.service;

import com.invoicepayment.payment.model.Payment;
import com.invoicepayment.payment.model.PaymentMethod;
import com.invoicepayment.payment.strategy.PaymentStrategy;

public interface PaymentService {
    Payment processPayment(Payment payment, PaymentMethod paymentMethod, PaymentStrategy strategy);
}

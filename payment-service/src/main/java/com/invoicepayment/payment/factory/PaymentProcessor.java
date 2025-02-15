package com.invoicepayment.payment.factory;

import com.invoicepayment.payment.dto.PaymentRequestDTO;
import com.invoicepayment.payment.model.Payment;

@FunctionalInterface
public interface PaymentProcessor {
    void process(PaymentRequestDTO payment);
}

package com.invoicepayment.payment.factory;

import com.invoicepayment.payment.dto.PaymentRequestDTO;

@FunctionalInterface
public interface PaymentProcessor {
    void process(PaymentRequestDTO payment);
}

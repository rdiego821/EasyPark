package com.invoicepayment.payment.factory;

import com.invoicepayment.payment.dto.PaymentRequestDTO;
import com.invoicepayment.payment.model.Payment;

public class CreditCardPaymentProcessor implements PaymentProcessor{
    @Override
    public void process(PaymentRequestDTO payment) {
        System.out.println("Processing credit card payment for: " + payment.getAmount());
    }
}

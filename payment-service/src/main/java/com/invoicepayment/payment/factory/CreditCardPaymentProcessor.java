package com.invoicepayment.payment.factory;

import com.invoicepayment.payment.model.Payment;

public class CreditCardPaymentProcessor implements PaymentProcessor{
    @Override
    public void process(Payment payment) {
        System.out.println("Processing credit card payment for: " + payment.getAmount());
    }
}

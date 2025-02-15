package com.invoicepayment.payment.factory;

import com.invoicepayment.payment.model.Payment;

public class PayPalPaymentProcessor implements PaymentProcessor{
    @Override
    public void process(Payment payment) {
        System.out.println("Processing Paypal payment for: " + payment.getAmount());
    }
}

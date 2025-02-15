package com.invoicepayment.payment.factory;

import com.invoicepayment.payment.dto.PaymentRequestDTO;
import com.invoicepayment.payment.model.Payment;

public class PayPalPaymentProcessor implements PaymentProcessor{
    @Override
    public void process(PaymentRequestDTO payment) {
        System.out.println("Processing Paypal payment for: " + payment.getAmount());
    }
}

package com.invoicepayment.payment.factory;

import com.invoicepayment.payment.model.Payment;

public class BankTransferProcessor implements PaymentProcessor{
    @Override
    public void process(Payment payment) {
        System.out.println("Processing Bank transfer payment for: " + payment.getAmount());
    }
}

package com.invoicepayment.payment.strategy;

public class StandardPaymentStrategy implements PaymentStrategy{
    @Override
    public double calculateFinalAmount(double amount) {
        return amount;
    }
}

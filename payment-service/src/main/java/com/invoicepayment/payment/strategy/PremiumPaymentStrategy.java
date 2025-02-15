package com.invoicepayment.payment.strategy;

public class PremiumPaymentStrategy implements PaymentStrategy{
    @Override
    public double calculateFinalAmount(double amount) {
        return amount * 0.95;
    }
}

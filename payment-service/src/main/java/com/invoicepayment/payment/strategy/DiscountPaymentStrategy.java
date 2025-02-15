package com.invoicepayment.payment.strategy;

public class DiscountPaymentStrategy implements PaymentStrategy{
    @Override
    public double calculateFinalAmount(double amount) {
        return amount * 0.9;
    }
}

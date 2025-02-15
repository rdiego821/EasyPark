package com.invoicepayment.payment.strategy;

@FunctionalInterface
public interface PaymentStrategy {
    double calculateFinalAmount(double amount);
}

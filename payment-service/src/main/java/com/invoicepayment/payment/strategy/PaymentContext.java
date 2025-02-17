package com.invoicepayment.payment.strategy;

public class PaymentContext {
    private final PaymentStrategy strategy;

    public PaymentContext(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public double executeStrategy(double amount) {
        return strategy.calculateFinalAmount(amount);
    }
}

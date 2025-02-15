package com.invoicepayment.payment.strategy;

import static com.invoicepayment.payment.util.PaymentConstants.PREMIUM_DISCOUNT_RATE;

public class PremiumPaymentStrategy implements PaymentStrategy{
    @Override
    public double calculateFinalAmount(double amount) {
        return amount * PREMIUM_DISCOUNT_RATE;
    }
}

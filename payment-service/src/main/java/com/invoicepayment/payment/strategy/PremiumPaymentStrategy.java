package com.invoicepayment.payment.strategy;

import static com.invoicepayment.payment.util.PaymentConstants.PREMIUM_SURCHARGE_RATE;

public class PremiumPaymentStrategy implements PaymentStrategy{
    @Override
    public double calculateFinalAmount(double amount) {
        return amount * PREMIUM_SURCHARGE_RATE;
    }
}

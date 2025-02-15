package com.invoicepayment.payment.strategy;

import static com.invoicepayment.payment.util.PaymentConstants.DISCOUNT_RATE;

public class DiscountPaymentStrategy implements PaymentStrategy{
    @Override
    public double calculateFinalAmount(double amount) {
        return amount * DISCOUNT_RATE;
    }
}

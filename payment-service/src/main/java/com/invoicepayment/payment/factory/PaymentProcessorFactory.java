package com.invoicepayment.payment.factory;

import com.invoicepayment.payment.model.PaymentMethod;

public class PaymentProcessorFactory {
    public static PaymentProcessor getProcessor(PaymentMethod type){
        return switch(type){
            case CREDIT_CARD -> new CreditCardPaymentProcessor();
            case PAYPAL -> new PayPalPaymentProcessor();
            case BANK_TRANSFER -> new BankTransferProcessor();
            default -> throw new IllegalArgumentException("Unsoported payment type: " + type);
        };
    }
}

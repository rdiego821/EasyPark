package com.invoicepayment.payment.factory;

import com.invoicepayment.payment.dto.PaymentRequestDTO;

public class BankTransferProcessor implements PaymentProcessor{
    @Override
    public void process(PaymentRequestDTO payment) {
        System.out.println("Processing Bank transfer payment for: " + payment.getAmount());
    }
}

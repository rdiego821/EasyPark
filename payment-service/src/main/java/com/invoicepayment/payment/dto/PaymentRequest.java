package com.invoicepayment.payment.dto;

import com.invoicepayment.payment.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentRequest {
    private Long invoiceId;
    private double amount;
    private PaymentMethod paymentMethod;
    private boolean premium;
}

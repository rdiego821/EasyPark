package com.invoicepayment.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDTO {
    private Long invoiceId;
    private Double amount;
}

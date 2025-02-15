package com.invoicepayment.payment.dto;

import com.invoicepayment.payment.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long invoiceId;
    private double amount;
    private PaymentStatus status;
    private LocalDateTime paymentDate;

    public PaymentRequestDTO(Long invoiceId, double amount, PaymentStatus status){
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.status = status;
    }
}

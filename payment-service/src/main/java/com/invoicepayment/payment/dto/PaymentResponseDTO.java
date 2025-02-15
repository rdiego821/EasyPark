package com.invoicepayment.payment.dto;

import com.invoicepayment.payment.model.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponseDTO {
    private Long id;
    private Long invoiceId;
    private Double amount;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
}

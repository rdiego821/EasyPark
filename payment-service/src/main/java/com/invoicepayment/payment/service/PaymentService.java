package com.invoicepayment.payment.service;

import com.invoicepayment.common.event.InvoiceEvent;
import com.invoicepayment.payment.dto.PaymentRequestDTO;
import com.invoicepayment.payment.dto.PaymentResponseDTO;
import com.invoicepayment.payment.model.Payment;
import com.invoicepayment.payment.model.PaymentMethod;
import com.invoicepayment.payment.strategy.PaymentStrategy;

import java.util.Optional;

public interface PaymentService {
    Payment processPayment(PaymentRequestDTO payment, PaymentMethod paymentMethod, PaymentStrategy strategy);
    PaymentResponseDTO submitPayment(PaymentRequestDTO payment, PaymentMethod paymentMethod, PaymentStrategy strategy);
    void handleInvoiceEvent(InvoiceEvent event);
    Optional<PaymentResponseDTO> getPaymentById(Long id);
}

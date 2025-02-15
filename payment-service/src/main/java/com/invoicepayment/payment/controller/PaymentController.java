package com.invoicepayment.payment.controller;

import com.invoicepayment.payment.dto.PaymentRequest;
import com.invoicepayment.payment.dto.PaymentRequestDTO;
import com.invoicepayment.payment.dto.PaymentResponseDTO;
import com.invoicepayment.payment.model.Payment;
import com.invoicepayment.payment.model.PaymentStatus;
import com.invoicepayment.payment.service.PaymentService;
import com.invoicepayment.payment.strategy.PaymentStrategy;
import com.invoicepayment.payment.strategy.PremiumPaymentStrategy;
import com.invoicepayment.payment.strategy.StandardPaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> makePayment(@RequestBody PaymentRequest request){
        PaymentStrategy strategy = request.isPremium() ? new PremiumPaymentStrategy() : new StandardPaymentStrategy();

        PaymentResponseDTO processedPayment = paymentService.submitPayment(
                new PaymentRequestDTO(request.getInvoiceId(), request.getAmount(), PaymentStatus.PENDING),
                request.getPaymentMethod(),
                strategy
        );

        log.info("Payment created with the id: {}", processedPayment.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(processedPayment);
    }
}

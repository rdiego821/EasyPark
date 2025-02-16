package com.invoicepayment.payment.controller;

import com.invoicepayment.payment.dto.PaymentRequest;
import com.invoicepayment.payment.dto.PaymentRequestDTO;
import com.invoicepayment.payment.dto.PaymentResponseDTO;
import com.invoicepayment.payment.model.PaymentStatus;
import com.invoicepayment.payment.service.PaymentService;
import com.invoicepayment.payment.strategy.PaymentStrategy;
import com.invoicepayment.payment.strategy.PremiumPaymentStrategy;
import com.invoicepayment.payment.strategy.StandardPaymentStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@Tag(name= "Payment API", description = "Operations related to payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Create payment", description = "Create payment")
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

    @Operation(summary = "Get payment by ID", description = "Retrieve a payment by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id){
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}

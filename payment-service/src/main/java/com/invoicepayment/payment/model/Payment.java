package com.invoicepayment.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long invoiceId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    public Payment() {
        this.paymentDate = LocalDateTime.now();
    }

    public Payment(Long invoiceId, Double amount, PaymentStatus status) {
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.status = status;
        this.paymentDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getInvoiceId() { return invoiceId; }
    public Double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getPaymentDate() { return paymentDate; }

    public void setId(Long id) { this.id = id; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public void setAmount(Double amount) { this.amount = amount; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
}

package com.invoicepayment.invoice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "invoice_items")
@Builder
@AllArgsConstructor
public class InvoiceItem {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column(nullable = false)
    private String productName;

    @Setter
    @Getter
    @Column(nullable = false)
    private String description;

    @Setter
    @Getter
    @Column(nullable = false)
    private double price;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonBackReference
    private Invoice invoice;

    public InvoiceItem() {}

    public InvoiceItem(String productName, double price, Invoice invoice) {
        this.productName = productName;
        this.price = price;
        this.invoice = invoice;
    }
}

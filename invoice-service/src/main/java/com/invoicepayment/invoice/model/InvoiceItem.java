package com.invoicepayment.invoice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private String productName;

    @Setter
    @Getter
    private String description;
    
    @Setter
    @Getter
    private double price;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    public InvoiceItem() {}

    public InvoiceItem(String productName, double price, Invoice invoice) {
        this.productName = productName;
        this.price = price;
        this.invoice = invoice;
    }
}

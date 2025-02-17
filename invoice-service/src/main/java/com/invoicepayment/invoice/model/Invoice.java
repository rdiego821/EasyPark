package com.invoicepayment.invoice.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "invoice")
@Builder
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private boolean paid;

    @Column(nullable = false)
    private double totalAmount;

    @Column(nullable = false)
    @Builder.Default
    private Date createdAt = new Date();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<InvoiceItem> items;

    public void calculateTotalAmount(){
        this.totalAmount = items.stream().mapToDouble(InvoiceItem::getPrice).sum();
    }

    public Invoice(){}

    public Invoice(String customerName, boolean paid){
        this.customerName = customerName;
        this.paid = paid;
    }
}

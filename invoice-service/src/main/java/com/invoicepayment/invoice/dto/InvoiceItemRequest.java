package com.invoicepayment.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InvoiceItemRequest {
    private String productName;
    private String description;
    private double price;
}

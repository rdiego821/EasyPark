package com.invoicepayment.invoice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceItemResponseDTO {
    private Long id;
    private String productName;
    private double price;
    private String description;
}

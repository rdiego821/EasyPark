package com.invoicepayment.invoice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class InvoiceResponseDTO {
    private Long id;
    private String customerName;
    private boolean paid;
    private double totalAmount;
    private Date createdAt = new Date();
    private List<InvoiceItemResponseDTO> invoiceItems;
}

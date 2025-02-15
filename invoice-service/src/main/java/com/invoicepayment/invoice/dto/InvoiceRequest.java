package com.invoicepayment.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class InvoiceRequest {
    private String customerName;
    private List<InvoiceItemRequest> invoiceItems;
}

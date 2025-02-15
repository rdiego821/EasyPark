package com.invoicepayment.invoice.service;

import com.invoicepayment.invoice.dto.InvoiceRequest;
import com.invoicepayment.invoice.model.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    Invoice createInvoice(InvoiceRequest request);
    List<Invoice> getAllInvoices();
    Optional<Invoice> getInvoiceById(Long id);
    List<Invoice> searchInvoices(String keyword);
}

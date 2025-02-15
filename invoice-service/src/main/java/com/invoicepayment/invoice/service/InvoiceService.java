package com.invoicepayment.invoice.service;

import com.invoicepayment.invoice.dto.InvoiceRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceResponseDTO;
import com.invoicepayment.invoice.model.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    Invoice createInvoice(InvoiceRequestDTO request);
    List<InvoiceResponseDTO> getAllInvoices();
    Optional<InvoiceResponseDTO> getInvoiceById(Long id);
    List<InvoiceResponseDTO> searchInvoices(String keyword);
}

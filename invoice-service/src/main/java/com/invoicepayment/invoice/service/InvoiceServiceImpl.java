package com.invoicepayment.invoice.service;

import com.invoicepayment.invoice.dto.InvoiceItemRequest;
import com.invoicepayment.invoice.dto.InvoiceRequest;
import com.invoicepayment.invoice.exception.InvoiceException;
import com.invoicepayment.invoice.model.Invoice;
import com.invoicepayment.invoice.model.InvoiceItem;
import com.invoicepayment.invoice.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService{
    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public Invoice createInvoice(InvoiceRequest request) {
        validateInvoiceRequest(request);
        Invoice invoice = buildInvoice(request);
        List<InvoiceItem> invoiceItems = createInvoiceItems(request.getInvoiceItems(), invoice);
        invoice.setItems(invoiceItems);
        invoice.calculateTotalAmount();
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices(){
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> getInvoiceById(Long id){
        return invoiceRepository.findById(id);
    }

    public List<Invoice> searchInvoices(String keyword){
        return invoiceRepository.searchByCustomerOrItemDescription(keyword, keyword);
    }

    private boolean itemsListIsNullOrEmpty(List<?> list){
        return list == null || list.isEmpty();
    }

    private void validateInvoiceRequest(InvoiceRequest request) {
        if (itemsListIsNullOrEmpty(request.getInvoiceItems())) {
            log.error("Error creating invoice. Invoice must contain at least one line item");
            throw new InvoiceException("Invoice must contain at least one line item.");
        }
    }

    private List<InvoiceItem> createInvoiceItems(List<InvoiceItemRequest> items, Invoice invoice) {
        return items.stream()
                .map(item -> mapToInvoiceItem(item, invoice))
                .toList();
    }

    private InvoiceItem mapToInvoiceItem(InvoiceItemRequest item, Invoice invoice) {
        return InvoiceItem.builder()
                .productName(item.getProductName())
                .price(item.getPrice())
                .description(item.getDescription())
                .invoice(invoice)
                .build();
    }

    private Invoice buildInvoice(InvoiceRequest request) {
        Invoice invoice = Invoice.builder()
                .customerName(request.getCustomerName())
                .build();

        List<InvoiceItem> invoiceItems = createInvoiceItems(request.getInvoiceItems(), invoice);
        invoice.setItems(invoiceItems);
        invoiceItems.forEach(item -> item.setInvoice(invoice));
        invoice.calculateTotalAmount();
        return invoice;
    }
}

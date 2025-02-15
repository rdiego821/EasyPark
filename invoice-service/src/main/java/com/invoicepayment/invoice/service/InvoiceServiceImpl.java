package com.invoicepayment.invoice.service;

import com.invoicepayment.invoice.dto.InvoiceRequest;
import com.invoicepayment.invoice.exception.InvalidInvoiceException;
import com.invoicepayment.invoice.model.Invoice;
import com.invoicepayment.invoice.model.InvoiceItem;
import com.invoicepayment.invoice.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService{
    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public Invoice createInvoice(InvoiceRequest request) {
        if (itemsListIsNullOrEmpty(request.getInvoiceItems())) {
            throw new InvalidInvoiceException("Invoice must contain at least one line item.");
        }

        Invoice invoice = new Invoice();
        invoice.setCustomerName(request.getCustomerName());

        List<InvoiceItem> invoiceItems = request.getInvoiceItems().stream()
                .map(item -> {
                    InvoiceItem invoiceItem = new InvoiceItem();
                    invoiceItem.setProductName(item.getProductName());
                    invoiceItem.setPrice(item.getPrice());
                    invoiceItem.setDescription(item.getDescription());
                    invoiceItem.setInvoice(invoice);
                    return invoiceItem;
                }).toList();

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
}

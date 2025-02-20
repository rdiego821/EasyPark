package com.invoicepayment.invoice.service;

import com.invoicepayment.invoice.model.InvoiceItem;
import com.invoicepayment.invoice.repository.InvoiceItemRepository;
import com.invoicepayment.invoice.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RefundService {

    private final InvoiceItemRepository invoiceItemRepository;
    private final InvoiceRepository invoiceRepository;

    public RefundService(InvoiceItemRepository invoiceItemRepository, InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
    }

    public List<InvoiceItem> processRefund(Long invoiceId){
        return invoiceRepository.findById(invoiceId).map(invoice -> {
            List<InvoiceItem> originalItems = invoice.getItems();

            List<InvoiceItem> refundItems = originalItems.stream().map(originalItem -> {
                InvoiceItem refundItem = new InvoiceItem();
                refundItem.setProductName(originalItem.getProductName());
                refundItem.setDescription(originalItem.getDescription());
                refundItem.setPrice(-originalItem.getPrice());
                invoice.calculateTotalAmount();
                refundItem.setInvoice(invoice);
                return refundItem;
            }).toList();

            refundItems = invoiceItemRepository.saveAll(refundItems);

            return refundItems;
        }).orElse(Collections.emptyList());
    }

}

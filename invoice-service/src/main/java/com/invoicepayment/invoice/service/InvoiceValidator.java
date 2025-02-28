package com.invoicepayment.invoice.service;

import ch.qos.logback.core.util.StringUtil;
import com.invoicepayment.invoice.dto.InvoiceItemRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceRequestDTO;
import com.invoicepayment.invoice.exception.InvoiceException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceValidator {

    public void validateInvoiceRequest(InvoiceRequestDTO dto) {
        if(StringUtil.isNullOrEmpty(dto.getCustomerName())){
            throw new InvoiceException("Customer name is required.");
        }

        if (itemsListIsNullOrEmpty(dto.getInvoiceItems())) {
            throw new InvoiceException("Invoice must contain at least one line item");
        }

        dto.getInvoiceItems().forEach(this::validateInvoiceItem);
    }

    private boolean itemsListIsNullOrEmpty(List<?> list){
        return list == null || list.isEmpty();
    }

    private void validateInvoiceItem(InvoiceItemRequestDTO item) {
        if(item.getPrice() <= 0){
            throw new InvoiceException("Item price must be greater than zero.");
        }
    }
}
package com.invoicepayment.invoice.mapper;

import com.invoicepayment.invoice.dto.InvoiceItemRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceItemResponseDTO;
import com.invoicepayment.invoice.dto.InvoiceResponseDTO;
import com.invoicepayment.invoice.model.Invoice;
import com.invoicepayment.invoice.model.InvoiceItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceMapper {


    public InvoiceItem mapDTOToInvoiceItem(InvoiceItemRequestDTO dto, Invoice invoice) {
        return InvoiceItem.builder()
                .productName(dto.getProductName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .invoice(invoice)
                .build();
    }

    public InvoiceResponseDTO convertToDTO(Invoice invoice) {
        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(invoice.getId());
        dto.setCustomerName(invoice.getCustomerName());
        dto.setPaid(invoice.isPaid());
        dto.setCreatedAt(invoice.getCreatedAt());
        invoice.calculateTotalAmount();
        dto.setTotalAmount(invoice.getTotalAmount());
        List<InvoiceItemResponseDTO> itemDTOs = invoice.getItems().stream()
                .map(this::convertToItemDTO)
                .toList();
        dto.setInvoiceItems(itemDTOs);
        return dto;
    }

    private InvoiceItemResponseDTO convertToItemDTO(InvoiceItem item) {
        InvoiceItemResponseDTO dto = new InvoiceItemResponseDTO();
        dto.setId(item.getId());
        dto.setProductName(item.getProductName());
        dto.setPrice(item.getPrice());
        dto.setDescription(item.getDescription());
        return dto;
    }
}
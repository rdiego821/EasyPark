package com.invoicepayment.invoice.service;

import ch.qos.logback.core.util.StringUtil;
import com.invoicepayment.common.event.InvoiceEvent;
import com.invoicepayment.invoice.dto.InvoiceItemRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceItemResponseDTO;
import com.invoicepayment.invoice.dto.InvoiceRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceResponseDTO;
import com.invoicepayment.invoice.exception.InvoiceException;
import com.invoicepayment.invoice.model.Invoice;
import com.invoicepayment.invoice.model.InvoiceItem;
import com.invoicepayment.invoice.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService{
    private final InvoiceRepository invoiceRepository;
    private final KafkaTemplate<String, InvoiceEvent> kafkaTemplate;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, KafkaTemplate<String, InvoiceEvent> kafkaTemplate) {
        this.invoiceRepository = invoiceRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Invoice createInvoice(InvoiceRequestDTO request) {
        validateInvoiceRequest(request);
        Invoice invoice = buildInvoice(request);
        List<InvoiceItem> invoiceItems = createInvoiceItems(request.getInvoiceItems(), invoice);
        invoice.setItems(invoiceItems);
        invoice.calculateTotalAmount();

        return invoiceRepository.save(invoice);
        //kafkaTemplate.send("invoice-events", new InvoiceEvent(savedInvoice.getId(), "INVOICE CREATED"));
    }

    public List<InvoiceResponseDTO> getAllInvoices(){
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<InvoiceResponseDTO> getInvoiceById(Long id){
        return invoiceRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<InvoiceResponseDTO> searchInvoices(String keyword){
        List<Invoice> invoices = invoiceRepository.searchByCustomerOrItemDescription(keyword);
        return invoices.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private boolean itemsListIsNullOrEmpty(List<?> list){
        return list == null || list.isEmpty();
    }

    private void validateInvoiceRequest(InvoiceRequestDTO dto) {
        if(StringUtil.isNullOrEmpty(dto.getCustomerName())){
            log.error("Customer name is required.");
            throw new InvoiceException("Customer name is required.");
        }

        if (itemsListIsNullOrEmpty(dto.getInvoiceItems())) {
            log.error("Invoice must contain at least one line item");
            throw new InvoiceException("Invoice must contain at least one line item.");
        }

        for(InvoiceItemRequestDTO item : dto.getInvoiceItems()){
            if(item.getPrice() <= 0){
                log.error("Item price must be greater than zero.");
                throw new InvoiceException("Item price must be greater than zero.");
            }
        }
    }

    private List<InvoiceItem> createInvoiceItems(List<InvoiceItemRequestDTO> itemsDTO, Invoice invoice) {
        return itemsDTO.stream()
                .map(dto -> mapDTOToInvoiceItem(dto, invoice))
                .toList();
    }

    private InvoiceItem mapDTOToInvoiceItem(InvoiceItemRequestDTO dto, Invoice invoice) {
        return InvoiceItem.builder()
                .productName(dto.getProductName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .invoice(invoice)
                .build();
    }

    private Invoice buildInvoice(InvoiceRequestDTO dto) {
        Invoice invoice = Invoice.builder()
                .customerName(dto.getCustomerName())
                .build();
        List<InvoiceItem> invoiceItems = createInvoiceItems(dto.getInvoiceItems(), invoice);
        invoice.setItems(invoiceItems);
        invoiceItems.forEach(item -> item.setInvoice(invoice));
        invoice.calculateTotalAmount();
        return invoice;
    }

    private InvoiceResponseDTO convertToDTO(Invoice invoice) {
        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(invoice.getId());
        dto.setCustomerName(invoice.getCustomerName());
        dto.setPaid(invoice.isPaid());
        dto.setCreatedAt(invoice.getCreatedAt());
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

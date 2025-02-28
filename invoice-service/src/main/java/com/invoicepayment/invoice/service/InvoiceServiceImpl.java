package com.invoicepayment.invoice.service;

import com.invoicepayment.common.event.InvoiceEvent;
import com.invoicepayment.invoice.dto.InvoiceItemRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceResponseDTO;
import com.invoicepayment.invoice.mapper.InvoiceMapper;
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
@Transactional
public class InvoiceServiceImpl implements InvoiceService{
    private final InvoiceRepository invoiceRepository;
    private final KafkaTemplate<String, InvoiceEvent> kafkaTemplate;
    private final InvoiceValidator invoiceValidator;
    private final InvoiceMapper invoiceMapper;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, KafkaTemplate<String, InvoiceEvent> kafkaTemplate, InvoiceValidator invoiceValidator, InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.invoiceValidator = invoiceValidator;
        this.invoiceMapper = invoiceMapper;
    }

    public Invoice createInvoice(InvoiceRequestDTO request) {
        invoiceValidator.validateInvoiceRequest(request);
        Invoice invoice = buildInvoice(request);
        List<InvoiceItem> invoiceItems = createInvoiceItems(request.getInvoiceItems(), invoice);
        invoice.setItems(invoiceItems);
        invoice.calculateTotalAmount();

        return invoiceRepository.save(invoice);
        //TODO Add kafka implementation
        //
        // kafkaTemplate.send("invoice-events", new InvoiceEvent(savedInvoice.getId(), "INVOICE CREATED"));
    }

    public List<InvoiceResponseDTO> getAllInvoices(){
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .map(invoiceMapper::convertToDTO)
                .toList();
    }

    public Optional<InvoiceResponseDTO> getInvoiceById(Long id){
        return invoiceRepository.findById(id)
                .map(invoiceMapper::convertToDTO);
    }

    public List<InvoiceResponseDTO> searchInvoices(String keyword){
        List<Invoice> invoices = invoiceRepository.searchByCustomerOrItemDescription(keyword);
        return invoices.stream()
                .map(invoiceMapper::convertToDTO)
                .toList();
    }

    private List<InvoiceItem> createInvoiceItems(List<InvoiceItemRequestDTO> itemsDTO, Invoice invoice) {
        return itemsDTO.stream()
                .map(dto -> invoiceMapper.mapDTOToInvoiceItem(dto, invoice))
                .toList();
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
}

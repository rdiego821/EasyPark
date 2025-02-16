package com.invoicepayment.invoice.service;

import com.invoicepayment.invoice.dto.InvoiceItemRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceResponseDTO;
import com.invoicepayment.invoice.event.InvoiceEvent;
import com.invoicepayment.invoice.exception.InvoiceException;
import com.invoicepayment.invoice.model.Invoice;
import com.invoicepayment.invoice.model.InvoiceItem;
import com.invoicepayment.invoice.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {
    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    InvoiceRequestDTO invoiceRequestDTO;

    @Mock
    KafkaTemplate<String, InvoiceEvent> kafkaTemplate;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private Invoice invoice;

    @BeforeEach
    void setUp() {
        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setCustomerName("John Doe");
        invoice.setCreatedAt(new Date());

        InvoiceItem item1 = new InvoiceItem("Item A", 100.0, invoice);
        InvoiceItem item2 = new InvoiceItem("Item B", 150.0, invoice);

        invoice.setItems(Arrays.asList(item1, item2));
        invoice.setTotalAmount(250.0);
    }

    @Test
    void createInvoice_ShouldSaveInvoice_WhenValidDataProvided() {
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        InvoiceRequestDTO request = new InvoiceRequestDTO();
        request.setCustomerName("Jhon Doe");

        InvoiceItemRequestDTO item1 = new InvoiceItemRequestDTO();
        item1.setProductName("Item A");
        item1.setDescription("laptop");
        item1.setPrice(250.0);
        request.setInvoiceItems(List.of(item1));

        Invoice savedInvoice = invoiceService.createInvoice(request);

        assertNotNull(savedInvoice);
        assertEquals(250.0, savedInvoice.getTotalAmount());
    }

    @Test
    void createInvoice_ShouldThrowException_WhenNoLineItems() {
        InvoiceRequestDTO emptyInvoice = new InvoiceRequestDTO();

        InvoiceException exception = assertThrows(InvoiceException.class, () -> {
            invoiceService.createInvoice(emptyInvoice);
        });

        assertEquals("Customer name is required.", exception.getMessage());
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void getInvoiceById_ShouldReturnInvoice_WhenIdExists() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        Optional<InvoiceResponseDTO> foundInvoice = invoiceService.getInvoiceById(1L);

        assertNotNull(foundInvoice);
        assertEquals(1L, foundInvoice.get().getId());
        verify(invoiceRepository, times(1)).findById(1L);
    }

    @Test
    void getAllInvoices_ShouldReturnInvoiceList() {
        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));

        List<InvoiceResponseDTO> result = invoiceService.getAllInvoices();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getCustomerName());

        verify(invoiceRepository, times(1)).findAll();
    }

    @Test
    void searchInvoices_ShouldReturnMatchingInvoices() {
        when(invoiceRepository.searchByCustomerOrItemDescription("John")).thenReturn(List.of(invoice));

        List<InvoiceResponseDTO> result = invoiceService.searchInvoices("John");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getCustomerName());

        verify(invoiceRepository, times(1)).searchByCustomerOrItemDescription("John");
    }

    @Test
    void validateInvoiceRequest_ShouldThrowException_WhenItemsAreNull() {
        InvoiceRequestDTO request = new InvoiceRequestDTO();
        request.setCustomerName("John Doe");
        request.setInvoiceItems(null);

        InvoiceException exception = assertThrows(InvoiceException.class, () -> {
            invoiceService.createInvoice(request);
        });

        assertEquals("Invoice must contain at least one line item.", exception.getMessage());
    }

    @Test
    void validateInvoiceRequest_ShouldThrowException_WhenItemsAreEmpty() {
        InvoiceRequestDTO request = new InvoiceRequestDTO();
        request.setCustomerName("John Doe");
        request.setInvoiceItems(Collections.emptyList());

        InvoiceException exception = assertThrows(InvoiceException.class, () -> {
            invoiceService.createInvoice(request);
        });

        assertEquals("Invoice must contain at least one line item.", exception.getMessage());
    }

    @Test
    void validateInvoiceRequest_ShouldThrowException_WhenItemPriceIsZeroOrNegative() {
        InvoiceItemRequestDTO invalidItem = new InvoiceItemRequestDTO();
        invalidItem.setProductName("Test Product");
        invalidItem.setPrice(0.0);

        InvoiceRequestDTO request = new InvoiceRequestDTO();
        request.setCustomerName("John Doe");
        request.setInvoiceItems(List.of(invalidItem));  // Lista con un item inválido

        InvoiceException exception = assertThrows(InvoiceException.class, () -> {
            invoiceService.createInvoice(request);
        });

        assertEquals("Item price must be greater than zero.", exception.getMessage());
    }
}

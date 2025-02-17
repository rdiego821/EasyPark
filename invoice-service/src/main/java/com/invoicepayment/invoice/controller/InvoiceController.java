package com.invoicepayment.invoice.controller;

import com.invoicepayment.invoice.dto.InvoiceRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceItemRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceItemRequest;
import com.invoicepayment.invoice.dto.InvoiceRequest;
import com.invoicepayment.invoice.dto.InvoiceResponseDTO;
import com.invoicepayment.invoice.exception.InvoiceException;
import com.invoicepayment.invoice.model.Invoice;
import com.invoicepayment.invoice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/invoices")
@Tag(name= "Invoice API", description = "Operations related to invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Operation(summary = "Get all invoices", description = "Retrieve a list of all invoices")
    @GetMapping
    @Cacheable(value = "invoicesCache")
    public ResponseEntity<List<InvoiceResponseDTO>> getAllInvoices(){
        return ResponseEntity.status(HttpStatus.OK).body(invoiceService.getAllInvoices());
    }

    @Operation(summary = "Get invoice by ID", description = "Retrieve an invoice by its ID")
    @GetMapping("/{id}")
    @Cacheable(value = "invoiceCache", key = "#id")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceById(@PathVariable Long id){
        return invoiceService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new InvoiceException("Invoice with ID " + id + " not found."));
    }

    @Operation(summary = "Get invoice by search criteria", description = "Retrieve an invoice by search criteria (customer name or item description)")
    @GetMapping("/search")
    public ResponseEntity<List<InvoiceResponseDTO>> searchInvoice(@RequestParam String keyword){
        return ResponseEntity.ok(invoiceService.searchInvoices(keyword));
    }

    @Operation(summary = "Create an invoice", description = "Create an invoice")
    @PostMapping
    @CacheEvict(value = "invoicesCache", allEntries = true)
    public ResponseEntity<Invoice> createInvoice(@RequestBody InvoiceRequest request){
        InvoiceRequestDTO invoiceRequestDTO = convertToDTO(request);
        Invoice createdInvoice = invoiceService.createInvoice(invoiceRequestDTO);
        log.info("Invoice created with the id: {}", createdInvoice.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInvoice);
    }

    private InvoiceRequestDTO convertToDTO(InvoiceRequest request) {
        InvoiceRequestDTO dto = new InvoiceRequestDTO();
        dto.setCustomerName(request.getCustomerName());

        List<InvoiceItemRequestDTO> itemDTOs = request.getInvoiceItems().stream()
                .map(this::convertItemToDTO)
                .toList();

        dto.setInvoiceItems(itemDTOs);
        return dto;
    }

    private InvoiceItemRequestDTO convertItemToDTO(InvoiceItemRequest item) {
        InvoiceItemRequestDTO itemDTO = new InvoiceItemRequestDTO();
        itemDTO.setProductName(item.getProductName());
        itemDTO.setPrice(item.getPrice());
        itemDTO.setDescription(item.getDescription());
        return itemDTO;
    }

}

package com.invoicepayment.invoice.controller;

import com.invoicepayment.invoice.dto.InvoiceRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceItemRequestDTO;
import com.invoicepayment.invoice.dto.InvoiceItemRequest;
import com.invoicepayment.invoice.dto.InvoiceRequest;
import com.invoicepayment.invoice.dto.InvoiceResponseDTO;
import com.invoicepayment.invoice.model.Invoice;
import com.invoicepayment.invoice.service.InvoiceService;
import lombok.extern.slf4j.Slf4j;
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
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponseDTO>> getAllInvoices(){
        return ResponseEntity.status(HttpStatus.OK).body(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceById(@PathVariable Long id){
        return invoiceService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<InvoiceResponseDTO>> searchInvoice(@RequestParam String keyword){
        return ResponseEntity.ok(invoiceService.searchInvoices(keyword));
    }

    @PostMapping
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

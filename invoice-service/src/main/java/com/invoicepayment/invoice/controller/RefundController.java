package com.invoicepayment.invoice.controller;

import com.invoicepayment.invoice.model.Invoice;
import com.invoicepayment.invoice.model.InvoiceItem;
import com.invoicepayment.invoice.service.InvoiceService;
import com.invoicepayment.invoice.service.RefundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/refund")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping
    public ResponseEntity<List<InvoiceItem>> doRefund(@RequestBody Long invoiceId){
        List<InvoiceItem> items = refundService.processRefund(invoiceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(items);
    }
}

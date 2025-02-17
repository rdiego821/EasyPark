package com.invoicepayment.invoice.event;

import com.invoicepayment.invoice.exception.InvoiceException;
import com.invoicepayment.invoice.model.Invoice;
import com.invoicepayment.invoice.repository.InvoiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.invoicepayment.common.event.PaymentEvent;

@Slf4j
@Component
public class PaymentEventListener {
    private final InvoiceRepository invoiceRepository;

    @Autowired
    public PaymentEventListener(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @KafkaListener(topics = "payment-events", groupId = "invoice-group")
    public void handlePaymentEvent(PaymentEvent event){
        log.info("Received Payment Event: {}", event);

        Invoice invoice = invoiceRepository.findById(event.getInvoiceId())
                .orElseThrow(() -> new InvoiceException("Invoice not found for payment"));

        if ("COMPLETED".equals(event.getStatus())) {
            invoice.setPaid(true);
            invoiceRepository.save(invoice);
            log.info("Invoice {} marked as PAID.", invoice.getId());
        }
    }
}

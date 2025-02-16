package com.invoicepayment.payment.event;

import com.invoicepayment.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.invoicepayment.common.event.InvoiceEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InvoiceEventListener {

    private final Map<Long, String> invoiceCache = new ConcurrentHashMap<>();
    private final PaymentService paymentService;

    public InvoiceEventListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "invoice-events", groupId = "payment-group")
    public void listenInvoiceCreated(InvoiceEvent event){
        log.info("Invoice received from kafka: {}", event);
        invoiceCache.put(event.getInvoiceId(), event.getMessage());
        paymentService.handleInvoiceEvent(event);
    }

    public boolean isInvoiceValid(Long invoiceId){
        return invoiceCache.containsKey(invoiceId);
    }
}

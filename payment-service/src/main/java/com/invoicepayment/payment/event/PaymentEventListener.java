package com.invoicepayment.payment.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.invoicepayment.common.event.PaymentEvent;

@Component
@Slf4j
public class PaymentEventListener {
    @KafkaListener(topics = "payment-events", groupId = "invoice-group")
    public void handlePaymentEvent(PaymentEvent event){
        log.info("Received Payment Event: {}", event);
    }
}

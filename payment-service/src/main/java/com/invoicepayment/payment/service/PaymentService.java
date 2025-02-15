package com.invoicepayment.payment.service;

import com.invoicepayment.payment.event.PaymentEvent;
import com.invoicepayment.payment.factory.PaymentProcessor;
import com.invoicepayment.payment.factory.PaymentProcessorFactory;
import com.invoicepayment.payment.model.Payment;
import com.invoicepayment.payment.model.PaymentMethod;
import com.invoicepayment.payment.model.PaymentStatus;
import com.invoicepayment.payment.repository.PaymentRepository;
import com.invoicepayment.payment.strategy.PaymentContext;
import com.invoicepayment.payment.strategy.PaymentStrategy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentService(PaymentRepository paymentRepository, KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Payment processPayment(Payment payment, PaymentMethod paymentMethod, PaymentStrategy strategy){
        PaymentProcessor processor = PaymentProcessorFactory.getProcessor(paymentMethod);
        processor.process(payment);

        PaymentContext context = new PaymentContext(strategy);
        double finalAmount = context.executeStrategy(payment.getAmount());
        payment.setAmount(finalAmount);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());

        /*
        kafkaTemplate.send("payment-events", new PaymentEvent(savedPayment.getId(),
                savedPayment.getInvoiceId(), savedPayment.getStatus().toString()));

         */
        return paymentRepository.save(payment);
    }
}

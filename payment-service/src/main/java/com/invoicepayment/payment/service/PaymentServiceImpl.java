package com.invoicepayment.payment.service;

import com.invoicepayment.payment.event.PaymentEvent;
import com.invoicepayment.payment.exception.PaymentException;
import com.invoicepayment.payment.factory.PaymentProcessor;
import com.invoicepayment.payment.factory.PaymentProcessorFactory;
import com.invoicepayment.payment.model.Payment;
import com.invoicepayment.payment.model.PaymentMethod;
import com.invoicepayment.payment.model.PaymentStatus;
import com.invoicepayment.payment.repository.PaymentRepository;
import com.invoicepayment.payment.strategy.PaymentContext;
import com.invoicepayment.payment.strategy.PaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService{
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentServiceImpl(PaymentRepository paymentRepository, KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Payment submitPayment(Payment payment, PaymentMethod paymentMethod, PaymentStrategy strategy) throws PaymentException {
        validatePayment(payment);
        return processPayment(payment, paymentMethod, strategy);
    }

    public Payment processPayment(Payment payment, PaymentMethod paymentMethod, PaymentStrategy strategy){
        try {
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
        } catch(Exception e){
            log.error("Error processing payment: {}", e.getMessage(), e);
            throw new PaymentException("Error processing payment: " + e.getMessage());
        }
    }

    private void validatePayment(Payment payment) throws PaymentException {
        if (payment == null) {
            throw new PaymentException("Payment cannot be null.");
        }
        if (payment.getAmount() <= 0) {
            throw new PaymentException("Payment amount must be greater than zero.");
        }
    }
}

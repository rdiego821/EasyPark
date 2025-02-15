package com.invoicepayment.payment.service;

import com.invoicepayment.payment.dto.PaymentRequestDTO;
import com.invoicepayment.payment.dto.PaymentResponseDTO;
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

    public PaymentResponseDTO submitPayment(PaymentRequestDTO payment, PaymentMethod paymentMethod, PaymentStrategy strategy) throws PaymentException {
        validatePayment(payment);
        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        Payment result = processPayment(payment, paymentMethod, strategy);
        convertToDTO(responseDTO, result);
        return responseDTO;
    }

    private static void convertToDTO(PaymentResponseDTO responseDTO, Payment result) {
        responseDTO.setId(result.getId());
        responseDTO.setInvoiceId(result.getInvoiceId());
        responseDTO.setAmount(result.getAmount());
        responseDTO.setStatus(result.getStatus());
        responseDTO.setPaymentDate(result.getPaymentDate());
    }

    public Payment processPayment(PaymentRequestDTO paymentRequestDTO, PaymentMethod paymentMethod, PaymentStrategy strategy){
        try {
            PaymentProcessor processor = PaymentProcessorFactory.getProcessor(paymentMethod);
            processor.process(paymentRequestDTO);

            PaymentContext context = new PaymentContext(strategy);
            double finalAmount = context.executeStrategy(paymentRequestDTO.getAmount());

            Payment payment = new Payment();
            payment.setInvoiceId(paymentRequestDTO.getInvoiceId());
            payment.setAmount(finalAmount);
            payment.setStatus(PaymentStatus.COMPLETED);

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

    private void validatePayment(PaymentRequestDTO payment) throws PaymentException {
        if (payment == null) {
            throw new PaymentException("Payment cannot be null.");
        }
        if (payment.getAmount() <= 0) {
            throw new PaymentException("Payment amount must be greater than zero.");
        }
    }
}

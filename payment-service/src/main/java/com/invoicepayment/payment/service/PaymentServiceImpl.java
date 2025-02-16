package com.invoicepayment.payment.service;

import com.invoicepayment.common.event.InvoiceEvent;
import com.invoicepayment.common.event.PaymentEvent;
import com.invoicepayment.payment.dto.PaymentRequestDTO;
import com.invoicepayment.payment.dto.PaymentResponseDTO;
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
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

import static com.invoicepayment.payment.util.PaymentConstants.INVOICE_CREATED;
import static com.invoicepayment.payment.util.PaymentConstants.INVOICE_SERVICE_BASE_URL;
import static com.invoicepayment.payment.util.PaymentConstants.INVOICE_SERVICE_GET_INVOICE_URI;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService{
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private final WebClient webClient;

    public PaymentServiceImpl(PaymentRepository paymentRepository, KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.webClient = WebClient.builder().baseUrl(INVOICE_SERVICE_BASE_URL).build();
    }

    @Override
    public PaymentResponseDTO submitPayment(PaymentRequestDTO payment, PaymentMethod paymentMethod, PaymentStrategy strategy) throws PaymentException {
        validatePayment(payment);
        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        Payment result = processPayment(payment, paymentMethod, strategy);
        convertToDTO(responseDTO, result);
        return responseDTO;
    }

    @Override
    public Payment processPayment(PaymentRequestDTO paymentRequestDTO, PaymentMethod paymentMethod, PaymentStrategy strategy){
        try {
            PaymentProcessor processor = PaymentProcessorFactory.getProcessor(paymentMethod);
            processor.process(paymentRequestDTO);
            PaymentContext context = new PaymentContext(strategy);
            double finalAmount = context.executeStrategy(paymentRequestDTO.getAmount());
            Payment payment = getPayment(paymentRequestDTO, finalAmount);
            Payment savedPayment = paymentRepository.save(payment);

            /*
            kafkaTemplate.send("payment-events", new PaymentEvent(savedPayment.getId(),
                    savedPayment.getInvoiceId(), savedPayment.getStatus().toString()));

             */

            return savedPayment;
        } catch(Exception e){
            log.error("Error processing payment: {}", e.getMessage(), e);
            throw new PaymentException("Error processing payment: " + e.getMessage());
        }
    }

    @Override
    public void handleInvoiceEvent(InvoiceEvent event){
        log.info("Processing invoice event for Invoice ID: {}", event.getInvoiceId());

        if(INVOICE_CREATED.equals(event.getMessage())){
            log.info("Invoice {} is ready for payment processing.", event.getInvoiceId());
        }
    }

    @Override
    public Optional<PaymentResponseDTO> getPaymentById(Long id){
        return paymentRepository.findById(id)
                .map(this::convertToDTO);
    }

    private void convertToDTO(PaymentResponseDTO responseDTO, Payment result) {
        responseDTO.setId(result.getId());
        responseDTO.setInvoiceId(result.getInvoiceId());
        responseDTO.setAmount(result.getAmount());
        responseDTO.setStatus(result.getStatus());
        responseDTO.setPaymentDate(result.getPaymentDate());
    }

    private Payment getPayment(PaymentRequestDTO paymentRequestDTO, double finalAmount) {
        Payment payment = new Payment();
        payment.setInvoiceId(paymentRequestDTO.getInvoiceId());
        payment.setAmount(finalAmount);
        payment.setStatus(PaymentStatus.PAID);
        return payment;
    }

    private PaymentResponseDTO convertToDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setStatus(payment.getStatus());
        dto.setAmount(payment.getAmount());
        dto.setInvoiceId(payment.getInvoiceId());
        dto.setId(payment.getId());
        return dto;
    }

    private void validatePayment(PaymentRequestDTO payment) throws PaymentException {
        if (payment == null) {
            throw new PaymentException("Payment cannot be null.");
        }
        if(payment.getInvoiceId() == null){
            throw new PaymentException("Invoice Id is required.");
        }
        if (payment.getAmount() <= 0) {
            throw new PaymentException("Payment amount must be greater than zero.");
        }

        if(!invoiceExists(payment.getInvoiceId())){
            throw new PaymentException("Invoice with id " + payment.getInvoiceId() + " not found.");
        }
    }

    private boolean invoiceExists(Long invoiceId){
        try{
            webClient.get()
                    .uri(INVOICE_SERVICE_GET_INVOICE_URI, invoiceId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (Exception e){
            return false;
        }
    }
}

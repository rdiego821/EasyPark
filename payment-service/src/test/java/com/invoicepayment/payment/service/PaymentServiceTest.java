package com.invoicepayment.payment.service;

import com.invoicepayment.common.event.PaymentEvent;
import com.invoicepayment.payment.dto.PaymentRequestDTO;
import com.invoicepayment.payment.dto.PaymentResponseDTO;
import com.invoicepayment.payment.exception.PaymentException;
import com.invoicepayment.payment.model.Payment;
import com.invoicepayment.payment.model.PaymentMethod;
import com.invoicepayment.payment.model.PaymentStatus;
import com.invoicepayment.payment.repository.PaymentRepository;
import com.invoicepayment.payment.strategy.PaymentStrategy;
import com.invoicepayment.payment.strategy.StandardPaymentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentStrategy strategy;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(1L);
        payment.setInvoiceId(100L);
        payment.setAmount(500.0);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
    }

    @Test
    void shouldSaveAndSendEventWhenValidPayment() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO(1L, 12.5, PaymentStatus.PENDING);

        Payment processedPayment = paymentService.processPayment(paymentRequestDTO, PaymentMethod.CREDIT_CARD,
                new StandardPaymentStrategy());

        assertNotNull(processedPayment);
        assertEquals(PaymentStatus.PENDING, processedPayment.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void shouldThrowExceptionWhenPaymentFails() {
        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException("DB error"));

        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO(1L, 12.5, PaymentStatus.PENDING);

        Exception exception = assertThrows(PaymentException.class, () ->
                paymentService.processPayment(paymentRequestDTO, PaymentMethod.CREDIT_CARD, new StandardPaymentStrategy())
        );

        assertEquals("Error processing payment: DB error", exception.getMessage());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void shouldThrowExceptionWhenPaymentIsInvalid() {
        PaymentRequestDTO request = new PaymentRequestDTO(123L, -100.0, PaymentStatus.PENDING);

        Exception exception = assertThrows(PaymentException.class, () ->
                paymentService.submitPayment(request, PaymentMethod.CREDIT_CARD, new StandardPaymentStrategy())
        );
        assertEquals("Payment amount must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPaymentIsNull() {
        PaymentRequestDTO request = null;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        PaymentStrategy paymentStrategy = new StandardPaymentStrategy();

        Exception exception = assertThrows(PaymentException.class, () ->
                paymentService.submitPayment(request, paymentMethod, paymentStrategy)
        );
        assertEquals("Payment cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenInvoiceIdIsNull() {
        PaymentRequestDTO request = new PaymentRequestDTO(null, 100.0, PaymentStatus.PENDING);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        PaymentStrategy paymentStrategy = new StandardPaymentStrategy();

        Exception exception = assertThrows(PaymentException.class, () ->
                paymentService.submitPayment(request, paymentMethod, paymentStrategy)
        );
        assertEquals("Invoice Id is required.", exception.getMessage());
    }

    @Test
    void shouldReturnPaymentWhenIdExists() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));

        Optional<PaymentResponseDTO> response = paymentService.getPaymentById(1L);

        assertTrue(response.isPresent());
        assertEquals(500.0, response.get().getAmount());
    }
}
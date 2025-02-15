package com.invoicepayment.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EmbeddedKafka(partitions = 1, topics = {"payment-events"}, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class PaymentServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApp.class, args);
    }
}

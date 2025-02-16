package com.invoicepayment.invoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class InvoiceServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(InvoiceServiceApp.class, args);
    }
}

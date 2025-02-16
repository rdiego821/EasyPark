package com.invoicepayment.payment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title= "Payment API",
                version = "1.0",
                description = "APIs for Payment Service"
        )
)
public class SwaggerConfig {
}

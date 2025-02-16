package com.invoicepayment.invoice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title= "Invoice API",
                version = "1.0",
                description = "APIs for Invoice Service"
        )
)
public class SwaggerConfig {
}

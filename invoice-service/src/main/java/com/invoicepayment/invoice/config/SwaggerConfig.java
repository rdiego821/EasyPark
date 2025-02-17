package com.invoicepayment.invoice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
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

        @Bean
        public OpenAPI customOpenAPI(){
                return new OpenAPI()
                        .info(new io.swagger.v3.oas.models.info.Info().description("test")
                                .title("Invoice & Payment API"));
        }
}

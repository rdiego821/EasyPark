package com.invoicepayment.invoice.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class InvoiceEvent implements Serializable {
    private Long invoiceId;
    private String message;

    @JsonCreator
    public InvoiceEvent(
            @JsonProperty("invoiceId") Long invoiceId,
            @JsonProperty("message") String message
    ) {
        this.invoiceId = invoiceId;
        this.message = message;
    }
}

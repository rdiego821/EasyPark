package com.invoicepayment.payment.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class PaymentEvent implements Serializable {
    private Long paymentId;
    private Long invoiceId;
    private String status;

    @JsonCreator
    public PaymentEvent(
            @JsonProperty("paymentId") Long paymentId,
            @JsonProperty("invoiceId") Long invoiceId,
            @JsonProperty("status") String status
    ) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.status = status;
    }
}

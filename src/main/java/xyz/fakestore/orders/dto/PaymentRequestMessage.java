package xyz.fakestore.orders.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Message published to orders.payments.paymentrequested.
 * Must match payments/dto/UserPaymentRequest.kt field names exactly.
 */
@Value
@Builder
public class PaymentRequestMessage {
    UUID userPaymentRequestId;
    UUID orderId;
    UUID userPaymentMethodId;
    BigDecimal amount;
    String currency;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant createdAt;
}

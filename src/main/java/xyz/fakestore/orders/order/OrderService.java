package xyz.fakestore.orders.order;

import org.springframework.stereotype.Service;
import xyz.fakestore.orders.dto.OrderRequest;
import xyz.fakestore.orders.dto.OrderResponse;
import xyz.fakestore.orders.dto.PaymentRequestMessage;
import xyz.fakestore.orders.kafka.KafkaMessageSender;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderService {

    private final KafkaMessageSender messageSender;

    public OrderService(KafkaMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public OrderResponse requestPayment(OrderRequest request) {
        UUID paymentRequestId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        String traceId = paymentRequestId.toString();

        PaymentRequestMessage message = PaymentRequestMessage.builder()
            .userPaymentRequestId(paymentRequestId)
            .orderId(orderId)
            .userPaymentMethodId(request.getUserPaymentMethodId())
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .createdAt(Instant.now())
            .build();

        messageSender.sendPaymentRequest(message, traceId);

        return new OrderResponse(paymentRequestId, orderId, "SUBMITTED");
    }
}

package xyz.fakestore.orders.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import xyz.fakestore.orders.dto.ShippingAuthMessage;
import xyz.fakestore.orders.order.OrderItemRecord;
import xyz.fakestore.orders.order.OrderRecord;

import java.time.Instant;
import java.util.List;

@Service
public class KafkaMessageSender {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageSender.class);

    static final String TOPIC_PAYMENT_REQUESTED = "orders.payments.paymentrequested";
    static final String TOPIC_SHIPPING_AUTHORIZED = "orders.shipping.authorized";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaMessageSender(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendPaymentRequest(Object message, String traceId) {
        send(TOPIC_PAYMENT_REQUESTED, null, message, traceId);
    }

    public void sendShippingAuthorized(OrderRecord order, List<OrderItemRecord> items, String traceId) {
        var message = ShippingAuthMessage.builder()
                .orderId(order.id())
                .userId(order.userId())
                .shippingAddressId(order.shippingAddressId())
                .total(order.amount())
                .items(items.stream().map(i -> ShippingAuthMessage.Item.builder()
                        .productId(i.productId())
                        .title(i.title())
                        .price(i.price())
                        .quantity(i.quantity())
                        .build()).toList())
                .createdAt(Instant.now())
                .build();
        send(TOPIC_SHIPPING_AUTHORIZED, order.id().toString(), message, traceId);
    }

    private void send(String topic, String key, Object message, String traceId) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            var future = key != null
                    ? kafkaTemplate.send(topic, key, payload)
                    : kafkaTemplate.send(topic, payload);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send to {} [traceId={}]: {}", topic, traceId, ex.getMessage());
                } else {
                    log.info("Sent to {} [traceId={}] offset={}", topic, traceId,
                            result.getRecordMetadata().offset());
                }
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize message for " + topic, e);
        }
    }
}

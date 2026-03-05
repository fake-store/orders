package xyz.fakestore.orders.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageSender {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageSender.class);

    static final String TOPIC_PAYMENT_REQUESTED = "orders.payments.paymentrequested";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaMessageSender(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendPaymentRequest(Object message, String traceId) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            var future = kafkaTemplate.send(TOPIC_PAYMENT_REQUESTED, payload);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send payment request [traceId={}]: {}", traceId, ex.getMessage());
                } else {
                    log.info("Payment request sent [traceId={}] offset={}", traceId,
                        result.getRecordMetadata().offset());
                }
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payment request", e);
        }
    }
}

package xyz.fakestore.orders.order;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentResultListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultListener.class);

    private final OrderService orderService;

    public PaymentResultListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(
        topics = {"payments.payment.processed", "payments.payment.rejected"},
        groupId = "orders-service"
    )
    public void onPaymentResult(ConsumerRecord<String, String> record) {
        String orderId = null;
        String status = null;
        String traceId = null;

        for (Header header : record.headers()) {
            switch (header.key()) {
                case "orderId"  -> orderId  = new String(header.value());
                case "status"   -> status   = new String(header.value());
                case "traceId"  -> traceId  = new String(header.value());
            }
        }

        if (orderId == null || status == null) {
            log.warn("Payment result missing orderId or status header, key={}", record.key());
            return;
        }

        if (traceId != null) MDC.put("traceId", traceId);
        MDC.put("orderId", orderId);
        try {
            orderService.handlePaymentResult(UUID.fromString(orderId), status, traceId);
            log.info("Handled payment result for order {} status={}", orderId, status);
        } finally {
            MDC.remove("orderId");
            if (traceId != null) MDC.remove("traceId");
        }
    }
}

package xyz.fakestore.orders.order;

import org.springframework.stereotype.Service;
import xyz.fakestore.orders.dto.OrderDetail;
import xyz.fakestore.orders.dto.OrderItemRequest;
import xyz.fakestore.orders.dto.OrderListItem;
import xyz.fakestore.orders.dto.OrderRequest;
import xyz.fakestore.orders.dto.OrderResponse;
import xyz.fakestore.orders.dto.PaymentRequestMessage;
import xyz.fakestore.orders.kafka.KafkaMessageSender;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final KafkaMessageSender messageSender;
    private final OrderRepository orderRepository;

    public OrderService(KafkaMessageSender messageSender, OrderRepository orderRepository) {
        this.messageSender = messageSender;
        this.orderRepository = orderRepository;
    }

    public OrderResponse requestPayment(OrderRequest request) {
        UUID paymentRequestId = UUID.randomUUID();
        BigDecimal total = request.getTotal();

        UUID orderId = orderRepository.insert(
                request.getUserId(),
                request.getUserPaymentMethodId(),
                paymentRequestId,
                request.getShippingAddressId(),
                total
        );

        for (OrderItemRequest item : request.getItems()) {
            orderRepository.insertItem(orderId, item);
        }

        PaymentRequestMessage message = PaymentRequestMessage.builder()
                .userPaymentRequestId(paymentRequestId)
                .orderId(orderId)
                .userPaymentMethodId(request.getUserPaymentMethodId())
                .amount(total)
                .currency("USD")
                .createdAt(Instant.now())
                .build();

        messageSender.sendPaymentRequest(message, paymentRequestId.toString());

        return new OrderResponse(paymentRequestId, orderId, "CREATED");
    }

    public void handlePaymentResult(UUID orderId, String paymentStatus, String traceId) {
        String orderStatus = "PROCESSED".equals(paymentStatus) ? "PAID" : "REJECTED";
        orderRepository.updateStatus(orderId, orderStatus);

        if ("PAID".equals(orderStatus)) {
            orderRepository.findById(orderId).ifPresent(order -> {
                List<OrderItemRecord> items = orderRepository.findItemsByOrderId(orderId);
                messageSender.sendShippingAuthorized(order, items, traceId);
            });
        }
    }

    public List<OrderListItem> getOrdersByUserId(UUID userId) {
        return orderRepository.findByUserId(userId);
    }

    public Optional<OrderListItem> getOrder(UUID orderId, UUID userId) {
        return orderRepository.findByIdForUser(orderId, userId);
    }

    public Optional<OrderDetail> getOrderDetail(UUID orderId, UUID userId) {
        return orderRepository.findDetailByIdForUser(orderId, userId);
    }

    public Optional<String> getOrderStatus(UUID orderId) {
        return orderRepository.findStatusById(orderId);
    }
}

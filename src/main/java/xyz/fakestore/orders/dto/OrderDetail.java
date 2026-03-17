package xyz.fakestore.orders.dto;

import xyz.fakestore.orders.order.OrderItemRecord;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDetail(
        UUID id,
        String status,
        BigDecimal amount,
        String currency,
        Instant createdAt,
        UUID paymentMethodId,
        UUID shippingAddressId,
        List<OrderItemRecord> items
) {}

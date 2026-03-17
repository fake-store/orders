package xyz.fakestore.orders.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemRecord(UUID productId, String title, BigDecimal price, int quantity) {}

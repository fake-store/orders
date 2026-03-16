package xyz.fakestore.orders.cart;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(UUID productId, String title, BigDecimal price, int quantity) {}

package xyz.fakestore.orders.dto;

import java.util.UUID;

public record OrderStatusResponse(UUID orderId, String status) {}

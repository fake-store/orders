package xyz.fakestore.orders.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class OrderResponse {
    UUID requestId;
    UUID orderId;
    String status;
}

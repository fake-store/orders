package xyz.fakestore.orders.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemRequest {
    private UUID productId;
    private String title;
    private BigDecimal price;
    private int quantity;
}

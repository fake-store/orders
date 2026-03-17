package xyz.fakestore.orders.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class ShippingAuthMessage {
    UUID orderId;
    UUID userId;
    UUID shippingAddressId;
    BigDecimal total;
    List<Item> items;
    Instant createdAt;

    @Value
    @Builder
    public static class Item {
        UUID productId;
        String title;
        BigDecimal price;
        int quantity;
    }
}

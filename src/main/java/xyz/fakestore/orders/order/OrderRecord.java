package xyz.fakestore.orders.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderRecord(UUID id, UUID userId, UUID shippingAddressId, BigDecimal amount) {}

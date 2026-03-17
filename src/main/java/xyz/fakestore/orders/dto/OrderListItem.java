package xyz.fakestore.orders.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderListItem(UUID id, String status, BigDecimal amount, String currency, Instant createdAt) {}

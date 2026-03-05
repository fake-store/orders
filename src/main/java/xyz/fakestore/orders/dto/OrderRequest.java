package xyz.fakestore.orders.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderRequest {
    private UUID userId;
    private UUID userPaymentMethodId;
    private BigDecimal amount;
    private String currency;
}

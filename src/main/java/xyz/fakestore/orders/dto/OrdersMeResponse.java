package xyz.fakestore.orders.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OrdersMeResponse {
    private String message;
    private UUID userId;
}

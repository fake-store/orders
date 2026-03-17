package xyz.fakestore.orders.order;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import xyz.fakestore.orders.dto.OrderDetail;
import xyz.fakestore.orders.dto.OrderListItem;
import xyz.fakestore.orders.dto.OrderRequest;
import xyz.fakestore.orders.dto.OrderResponse;
import xyz.fakestore.orders.dto.OrderStatusResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/request-payment")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponse requestPayment(
            @RequestBody OrderRequest request,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        if (traceId != null) MDC.put("traceId", traceId);
        try {
            OrderResponse response = orderService.requestPayment(request);
            MDC.put("orderId", response.getOrderId().toString());
            return response;
        } finally {
            MDC.remove("orderId");
            if (traceId != null) MDC.remove("traceId");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetail> getOrder(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        if (traceId != null) MDC.put("traceId", traceId);
        MDC.put("orderId", id.toString());
        try {
            UUID userId = UUID.fromString(
                    (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return orderService.getOrderDetail(id, userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } finally {
            MDC.remove("orderId");
            if (traceId != null) MDC.remove("traceId");
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> getStatus(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        if (traceId != null) MDC.put("traceId", traceId);
        MDC.put("orderId", id.toString());
        try {
            return orderService.getOrderStatus(id)
                    .map(status -> ResponseEntity.ok(new OrderStatusResponse(id, status)))
                    .orElse(ResponseEntity.notFound().build());
        } finally {
            MDC.remove("orderId");
            if (traceId != null) MDC.remove("traceId");
        }
    }

    @GetMapping("/me")
    public List<OrderListItem> me(
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        if (traceId != null) MDC.put("traceId", traceId);
        try {
            UUID userId = UUID.fromString(
                    (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return orderService.getOrdersByUserId(userId);
        } finally {
            if (traceId != null) MDC.remove("traceId");
        }
    }
}

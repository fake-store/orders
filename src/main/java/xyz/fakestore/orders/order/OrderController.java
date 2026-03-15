package xyz.fakestore.orders.order;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import xyz.fakestore.orders.dto.OrderRequest;
import xyz.fakestore.orders.dto.OrderResponse;
import xyz.fakestore.orders.dto.OrdersMeResponse;

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
            return orderService.requestPayment(request);
        } finally {
            if (traceId != null) MDC.remove("traceId");
        }
    }

    @GetMapping("/me")
    public OrdersMeResponse me(@RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        if (traceId != null) MDC.put("traceId", traceId);
        try {
            String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return new OrdersMeResponse("Hello from Orders Service!", UUID.fromString(userId));
        } finally {
            if (traceId != null) MDC.remove("traceId");
        }
    }
}

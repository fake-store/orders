package xyz.fakestore.orders.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import xyz.fakestore.orders.dto.OrderRequest;
import xyz.fakestore.orders.dto.OrderResponse;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/request-payment")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponse requestPayment(@RequestBody OrderRequest request) {
        return orderService.requestPayment(request);
    }
}

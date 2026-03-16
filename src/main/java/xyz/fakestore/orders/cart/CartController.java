package xyz.fakestore.orders.cart;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartRepository cartRepository;

    public CartController(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @GetMapping
    public List<CartItemResponse> getCart(
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        if (traceId != null) MDC.put("traceId", traceId);
        try {
            return cartRepository.getItems(userId());
        } finally {
            if (traceId != null) MDC.remove("traceId");
        }
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public void addItem(
            @RequestBody CartItemRequest item,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        if (traceId != null) MDC.put("traceId", traceId);
        try {
            cartRepository.upsertItem(userId(), item);
        } finally {
            if (traceId != null) MDC.remove("traceId");
        }
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(
            @PathVariable UUID productId,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        if (traceId != null) MDC.put("traceId", traceId);
        try {
            cartRepository.removeItem(userId(), productId);
        } finally {
            if (traceId != null) MDC.remove("traceId");
        }
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        if (traceId != null) MDC.put("traceId", traceId);
        try {
            cartRepository.clearCart(userId());
        } finally {
            if (traceId != null) MDC.remove("traceId");
        }
    }

    @PostMapping("/merge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void mergeItems(
            @RequestBody List<CartItemRequest> items,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        if (traceId != null) MDC.put("traceId", traceId);
        try {
            UUID uid = userId();
            for (CartItemRequest item : items) {
                cartRepository.upsertItem(uid, item);
            }
        } finally {
            if (traceId != null) MDC.remove("traceId");
        }
    }

    private UUID userId() {
        return UUID.fromString(
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}

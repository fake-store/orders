package xyz.fakestore.orders.cart;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public class CartRepository {

    private static final Table<?> CART_ITEMS = DSL.table("cart_items");
    private static final Field<UUID> USER_ID = DSL.field("user_id", UUID.class);
    private static final Field<UUID> PRODUCT_ID = DSL.field("product_id", UUID.class);
    private static final Field<String> TITLE = DSL.field("title", String.class);
    private static final Field<BigDecimal> PRICE = DSL.field("price", BigDecimal.class);
    private static final Field<Integer> QUANTITY = DSL.field("quantity", Integer.class);

    private final DSLContext dsl;

    public CartRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<CartItemResponse> getItems(UUID userId) {
        return dsl.select(USER_ID, PRODUCT_ID, TITLE, PRICE, QUANTITY)
                .from(CART_ITEMS)
                .where(USER_ID.eq(userId))
                .fetch(r -> new CartItemResponse(
                        r.get(PRODUCT_ID), r.get(TITLE), r.get(PRICE), r.get(QUANTITY)));
    }

    public void upsertItem(UUID userId, CartItemRequest item) {
        dsl.insertInto(CART_ITEMS)
                .set(USER_ID, userId)
                .set(PRODUCT_ID, item.productId())
                .set(TITLE, item.title())
                .set(PRICE, item.price())
                .set(QUANTITY, item.quantity())
                .onConflict(USER_ID, PRODUCT_ID)
                .doUpdate()
                .set(QUANTITY, DSL.field("cart_items.quantity", Integer.class).add(item.quantity()))
                .set(TITLE, item.title())
                .set(PRICE, item.price())
                .execute();
    }

    public void removeItem(UUID userId, UUID productId) {
        dsl.deleteFrom(CART_ITEMS)
                .where(USER_ID.eq(userId))
                .and(PRODUCT_ID.eq(productId))
                .execute();
    }

    public void clearCart(UUID userId) {
        dsl.deleteFrom(CART_ITEMS)
                .where(USER_ID.eq(userId))
                .execute();
    }
}

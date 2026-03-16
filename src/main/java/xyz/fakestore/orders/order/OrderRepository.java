package xyz.fakestore.orders.order;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import xyz.fakestore.orders.dto.OrderDetail;
import xyz.fakestore.orders.dto.OrderItemRequest;
import xyz.fakestore.orders.dto.OrderListItem;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepository {

    // orders table
    private static final Table<?> ORDERS = DSL.table("orders");
    private static final Field<UUID> ID = DSL.field("id", UUID.class);
    private static final Field<UUID> USER_ID = DSL.field("user_id", UUID.class);
    private static final Field<UUID> PAYMENT_METHOD_ID = DSL.field("payment_method_id", UUID.class);
    private static final Field<UUID> PAYMENT_REQUEST_ID = DSL.field("payment_request_id", UUID.class);
    private static final Field<UUID> SHIPPING_ADDRESS_ID = DSL.field("shipping_address_id", UUID.class);
    private static final Field<BigDecimal> AMOUNT = DSL.field("amount", BigDecimal.class);
    private static final Field<String> CURRENCY = DSL.field("currency", String.class);
    private static final Field<String> STATUS = DSL.field("status", String.class);
    private static final Field<OffsetDateTime> CREATED_AT = DSL.field("created_at", OffsetDateTime.class);
    private static final Field<OffsetDateTime> UPDATED_AT = DSL.field("updated_at", OffsetDateTime.class);

    // order_items table
    private static final Table<?> ORDER_ITEMS = DSL.table("order_items");
    private static final Field<UUID> ITEM_ID = DSL.field("id", UUID.class);
    private static final Field<UUID> ITEM_ORDER_ID = DSL.field("order_id", UUID.class);
    private static final Field<UUID> ITEM_PRODUCT_ID = DSL.field("product_id", UUID.class);
    private static final Field<String> ITEM_TITLE = DSL.field("title", String.class);
    private static final Field<BigDecimal> ITEM_PRICE = DSL.field("price", BigDecimal.class);
    private static final Field<Integer> ITEM_QUANTITY = DSL.field("quantity", Integer.class);

    private final DSLContext dsl;

    public OrderRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public UUID insert(UUID userId, UUID paymentMethodId, UUID paymentRequestId,
                       UUID shippingAddressId, BigDecimal amount) {
        return dsl.insertInto(ORDERS)
                .columns(USER_ID, PAYMENT_METHOD_ID, PAYMENT_REQUEST_ID, SHIPPING_ADDRESS_ID,
                        AMOUNT, CURRENCY, STATUS)
                .values(userId, paymentMethodId, paymentRequestId, shippingAddressId,
                        amount, "USD", "CREATED")
                .returning(ID)
                .fetchOne(ID);
    }

    public void insertItem(UUID orderId, OrderItemRequest item) {
        dsl.insertInto(ORDER_ITEMS)
                .columns(ITEM_ORDER_ID, ITEM_PRODUCT_ID, ITEM_TITLE, ITEM_PRICE, ITEM_QUANTITY)
                .values(orderId, item.getProductId(), item.getTitle(), item.getPrice(), item.getQuantity())
                .execute();
    }

    public Optional<OrderRecord> findById(UUID orderId) {
        return dsl.select(ID, USER_ID, SHIPPING_ADDRESS_ID, AMOUNT)
                .from(ORDERS)
                .where(ID.eq(orderId))
                .fetchOptional(r -> new OrderRecord(
                        r.get(ID), r.get(USER_ID), r.get(SHIPPING_ADDRESS_ID), r.get(AMOUNT)));
    }

    public List<OrderItemRecord> findItemsByOrderId(UUID orderId) {
        return dsl.select(ITEM_PRODUCT_ID, ITEM_TITLE, ITEM_PRICE, ITEM_QUANTITY)
                .from(ORDER_ITEMS)
                .where(ITEM_ORDER_ID.eq(orderId))
                .fetch(r -> new OrderItemRecord(
                        r.get(ITEM_PRODUCT_ID), r.get(ITEM_TITLE),
                        r.get(ITEM_PRICE), r.get(ITEM_QUANTITY)));
    }

    public List<OrderListItem> findByUserId(UUID userId) {
        return dsl.select(ID, STATUS, AMOUNT, CURRENCY, CREATED_AT)
                .from(ORDERS)
                .where(USER_ID.eq(userId))
                .orderBy(CREATED_AT.desc())
                .fetch(r -> new OrderListItem(
                        r.get(ID), r.get(STATUS), r.get(AMOUNT),
                        r.get(CURRENCY), r.get(CREATED_AT).toInstant()));
    }

    public Optional<OrderListItem> findByIdForUser(UUID orderId, UUID userId) {
        return dsl.select(ID, STATUS, AMOUNT, CURRENCY, CREATED_AT)
                .from(ORDERS)
                .where(ID.eq(orderId).and(USER_ID.eq(userId)))
                .fetchOptional(r -> new OrderListItem(
                        r.get(ID), r.get(STATUS), r.get(AMOUNT),
                        r.get(CURRENCY), r.get(CREATED_AT).toInstant()));
    }

    public Optional<OrderDetail> findDetailByIdForUser(UUID orderId, UUID userId) {
        return dsl.select(ID, STATUS, AMOUNT, CURRENCY, CREATED_AT, PAYMENT_METHOD_ID, SHIPPING_ADDRESS_ID)
                .from(ORDERS)
                .where(ID.eq(orderId).and(USER_ID.eq(userId)))
                .fetchOptional(r -> {
                    List<OrderItemRecord> items = dsl
                            .select(ITEM_PRODUCT_ID, ITEM_TITLE, ITEM_PRICE, ITEM_QUANTITY)
                            .from(ORDER_ITEMS)
                            .where(ITEM_ORDER_ID.eq(orderId))
                            .fetch(ir -> new OrderItemRecord(
                                    ir.get(ITEM_PRODUCT_ID), ir.get(ITEM_TITLE),
                                    ir.get(ITEM_PRICE), ir.get(ITEM_QUANTITY)));
                    return new OrderDetail(
                            r.get(ID), r.get(STATUS), r.get(AMOUNT), r.get(CURRENCY),
                            r.get(CREATED_AT).toInstant(), r.get(PAYMENT_METHOD_ID),
                            r.get(SHIPPING_ADDRESS_ID), items);
                });
    }

    public Optional<String> findStatusById(UUID orderId) {
        return dsl.select(STATUS)
                .from(ORDERS)
                .where(ID.eq(orderId))
                .fetchOptional(r -> r.get(STATUS));
    }

    public void updateStatus(UUID orderId, String status) {
        dsl.update(ORDERS)
                .set(STATUS, status)
                .set(UPDATED_AT, OffsetDateTime.now())
                .where(ID.eq(orderId))
                .execute();
    }
}

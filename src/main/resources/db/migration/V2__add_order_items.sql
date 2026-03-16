ALTER TABLE orders ADD COLUMN shipping_address_id UUID;

CREATE TABLE order_items (
    id         UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id   UUID           NOT NULL REFERENCES orders(id),
    product_id UUID           NOT NULL,
    title      VARCHAR(255)   NOT NULL,
    price      NUMERIC(10, 2) NOT NULL,
    quantity   INT            NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE order_items TO fakestore_orders_user;

CREATE TABLE cart_items (
    user_id    UUID          NOT NULL,
    product_id UUID          NOT NULL,
    title      VARCHAR(255)  NOT NULL,
    price      NUMERIC(10,2) NOT NULL,
    quantity   INT           NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, product_id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE cart_items TO fakestore_orders_user;

CREATE TABLE orders (
    id                 UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id            UUID        NOT NULL,
    payment_method_id  UUID        NOT NULL,
    payment_request_id UUID        NOT NULL UNIQUE,
    amount             NUMERIC(10, 2) NOT NULL,
    currency           VARCHAR(10) NOT NULL,
    status             VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE orders TO fakestore_orders_user;

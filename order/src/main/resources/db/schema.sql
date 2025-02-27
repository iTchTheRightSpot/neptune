DROP TABLE IF EXISTS order_table;

CREATE TABLE order_table
(
    order_id   SERIAL     NOT NULL UNIQUE PRIMARY KEY,
    uuid       UUID       NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    product_id UUID       NOT NULL,
    qty        SMALLINT                   DEFAULT 0 CHECK (qty > 0),
    status     VARCHAR(9) NOT NULL
);

CREATE INDEX IX_order_uuid ON order_table (uuid);
CREATE INDEX IX_order_product_id ON order_table (product_id);
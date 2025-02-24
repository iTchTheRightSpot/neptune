DROP TABLE IF EXISTS inventory;

CREATE TABLE inventory
(
    inventory_id SERIAL NOT NULL UNIQUE PRIMARY KEY,
    uuid         UUID UNIQUE DEFAULT gen_random_uuid(),
    name         VARCHAR(50) UNIQUE,
    qty          SMALLINT DEFAULT 0 CHECK (qty > 0)
);
CREATE INDEX IX_inventory_name ON inventory (uuid);
CREATE INDEX IX_inventory_uuid ON inventory (name);
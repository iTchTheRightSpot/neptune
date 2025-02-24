DROP TABLE IF EXISTS inventory;

CREATE TABLE inventory
(
    inventory_id SERIAL      NOT NULL UNIQUE PRIMARY KEY,
    name         VARCHAR(50) NOT NULL UNIQUE,
    uuid         UUID        NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    qty          SMALLINT    NOT NULL        DEFAULT 0 CHECK (qty > 0)
);

CREATE INDEX IX_inventory_name ON inventory (name);
CREATE INDEX IX_inventory_uuid ON inventory (uuid);
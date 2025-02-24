package org.inventory;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("inventory")
record Inventory(
        @Id
        @Column("inventory_id")
        Integer inventoryId,
        @Column("uuid")
        UUID uuid,
        @Column("name")
        String name,
        @Column("qty")
        int qty
) {
        public Inventory(UUID uuid, String name, int qty) {
                this(null, uuid, name, qty);
        }
}
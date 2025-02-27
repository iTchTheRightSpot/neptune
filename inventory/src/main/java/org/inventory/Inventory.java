package org.inventory;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("inventory")
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Inventory {
        @Id
        @Column("inventory_id")
        private Integer inventoryId;
        @Column("uuid")
        private UUID uuid;
        @Column("name")
        private String name;
        @Column("qty")
        private short qty;

        public Inventory(final UUID uuid, final String name, final short qty) {
                this(null, uuid, name, qty);
        }

        public Integer inventoryId() {
                return inventoryId;
        }

        public UUID uuid() {
                return uuid;
        }

        public String name() {
                return name;
        }

        public short qty() {
                return qty;
        }
}
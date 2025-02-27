package org.order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("order_table")
@Setter
@AllArgsConstructor
@NoArgsConstructor
class Order {
    @Id
    @Column("order_id")
    private Integer orderId;
    @Column("uuid")
    private UUID uuid;
    @Column("product_id")
    private UUID productId;
    @Column("qty")
    private short qty;
    @Column("status")
    private OrderStatus status;

    public Order(final UUID uuid, final UUID productId, final short qty, final OrderStatus status) {
        this(null, uuid, productId, qty, status);
    }

    public UUID uuid() {
        return uuid;
    }

    public UUID productId() {
        return productId;
    }

    public short qty() {
        return qty;
    }

    public OrderStatus status() {
        return status;
    }
}

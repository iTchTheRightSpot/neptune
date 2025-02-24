package org.order.inventory;

import java.util.Optional;
import java.util.UUID;

public interface InventoryService {
    Optional<Inventory> inventoryByUUID(final UUID uuid);
    void deductQty(final UUID productId, final short qty);
}

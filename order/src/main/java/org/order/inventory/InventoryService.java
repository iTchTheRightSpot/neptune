package org.order.inventory;

import java.util.UUID;

public interface InventoryService {
    Inventory inventoryByUUID(final UUID uuid);
}

package org.order.inventory;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class InventoryServiceImpl implements InventoryService {

    @Override
    public Inventory inventoryByUUID(final UUID uuid) {
        return null;
    }
}

package org.order.inventory;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
class InventoryServiceImpl implements InventoryService {

    @Override
    public Optional<Inventory> inventoryByUUID(final UUID uuid) {
        return Optional.empty();
    }

    @Override
    public void deductQty(final UUID productId, final short qty) {
        throw new UnsupportedOperationException("not supported yet implemented.");
    }
}

package org.inventory;

import lombok.RequiredArgsConstructor;
import org.inventory.exception.InsertionException;
import org.inventory.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class InventoryService {

    private final InventoryStore store;

    public List<InventoryResponse> all() {
        final List<InventoryResponse> list = new ArrayList<>();
        store.findAll().forEach(a -> list.add(new InventoryResponse(a.uuid().toString(), a.name(), a.qty())));
        return list;
    }

    public void create(final InventoryPayload dto) {
        try {
            store.save(new Inventory(UUID.randomUUID(), dto.name().trim(), dto.qty()));
        } catch (final Exception e) {
            throw new InsertionException();
        }
    }

    public InventoryResponse inventoryById(final String productId) {
        return store.inventoryByUUID(productId.trim())
                .map(inventory -> new InventoryResponse(inventory.uuid().toString(), inventory.name(), inventory.qty()))
                .orElseThrow(NotFoundException::new);
    }
}
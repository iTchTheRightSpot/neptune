package org.inventory;

import lombok.RequiredArgsConstructor;
import org.inventory.exception.BadRequestException;
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

    List<InventoryResponse> all() {
        final List<InventoryResponse> list = new ArrayList<>();
        store.findAll().forEach(a -> list.add(new InventoryResponse(a.uuid().toString(), a.name(), a.qty())));
        return list;
    }

    void create(final InventoryPayload dto) {
        try {
            store.save(new Inventory(UUID.randomUUID(), dto.name().trim(), dto.qty()));
        } catch (final Exception e) {
            throw new InsertionException();
        }
    }

    InventoryResponse inventoryById(final String productId) {
        final UUID uuid;
        try {
            uuid = UUID.fromString(productId.trim());
        } catch (final IllegalArgumentException e) {
            throw new BadRequestException();
        }

        return store.inventoryByUUID(uuid)
                .map(inventory -> new InventoryResponse(inventory.uuid().toString(), inventory.name(), inventory.qty()))
                .orElseThrow(NotFoundException::new);
    }
}
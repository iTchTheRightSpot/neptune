package org.inventory;

import lombok.RequiredArgsConstructor;
import org.inventory.exception.BadRequestException;
import org.inventory.exception.InsertionException;
import org.inventory.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryStore store;

    List<InventoryResponsePayload> all() {
        final List<InventoryResponsePayload> list = new ArrayList<>();
        store.findAll().forEach(a -> list.add(new InventoryResponsePayload(a.uuid().toString(), a.name(), a.qty())));
        return list;
    }

    void create(final InventoryRequestPayload dto) {
        try {
            store.save(new Inventory(UUID.randomUUID(), dto.name().trim(), dto.qty()));
        } catch (final RuntimeException e) {
            log.error(e.getMessage());
            throw new InsertionException();
        }
    }

    InventoryResponsePayload inventoryById(final String productId) {
        final UUID uuid;
        try {
            uuid = UUID.fromString(productId.trim());
        } catch (final IllegalArgumentException e) {
            throw new BadRequestException();
        }
        return store.inventoryByUUID(uuid).map(inv -> new InventoryResponsePayload(inv.uuid().toString(), inv.name(), inv.qty())).orElseThrow(NotFoundException::new);
    }
}
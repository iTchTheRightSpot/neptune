package org.order;

import lombok.RequiredArgsConstructor;
import org.order.exception.BadRequestException;
import org.order.exception.InsertionException;
import org.order.exception.NotFoundException;
import org.order.inventory.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class OrderService {

    private final OrderStore store;
    private final InventoryService service;

    List<OrderResponse> all() {
        final List<OrderResponse> list = new ArrayList<>();
        store.findAll().forEach(a -> list.add(new OrderResponse(a.uuid().toString(), a.qty(), a.status())));
        return list;
    }

    @Transactional(rollbackFor = InsertionException.class)
    void create(final OrderPayload dto) {
        final UUID productId;
        try {
            productId = UUID.fromString(dto.productId().trim());
        } catch (final IllegalArgumentException e) {
            throw new NotFoundException("invalid product id");
        }

        final var inventory = service.inventoryByUUID(productId).orElseThrow(() -> new NotFoundException("invalid product id"));
        if (inventory.qty() < dto.qty()) throw new BadRequestException("qty greater than inventory");

        try {
            store.save(new Order(UUID.randomUUID(), productId, dto.qty(), dto.status()));
            service.deductQty(productId, dto.qty());
        } catch (final Exception e) {
            throw new InsertionException();
        }
    }
}

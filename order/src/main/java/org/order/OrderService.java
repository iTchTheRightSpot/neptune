package org.order;

import lombok.RequiredArgsConstructor;
import org.order.exception.BadRequestException;
import org.order.exception.InsertionException;
import org.order.exception.NotFoundException;
import org.order.inventory.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderStore store;
    private final InventoryService service;

    List<OrderResponsePayload> all() {
        final List<OrderResponsePayload> list = new ArrayList<>();
        store.findAll().forEach(a -> list.add(new OrderResponsePayload(a.uuid().toString(), a.qty(), a.status())));
        return list;
    }

    @Transactional(rollbackFor = InsertionException.class)
    void create(final OrderRequestPayload dto) {
        final UUID productId;
        try {
            productId = UUID.fromString(dto.productId().trim());
        } catch (final IllegalArgumentException e) {
            throw new NotFoundException("invalid product id");
        }

        final var inventory = service.inventoryByUUID(productId).orElseThrow(() -> new NotFoundException("invalid product id"));
        if (inventory.qty() == 0) throw new BadRequestException("out of stock");

        try {
            store.save(new Order(UUID.randomUUID(), productId, dto.qty(), dto.status()));
            service.deductQty(productId, dto.qty());
        } catch (final DataIntegrityViolationException | InsertionException e) {
            log.error(e.getMessage());
            final var message = e instanceof DataIntegrityViolationException
                    ? "error creating order please check request body" : e.getMessage();
            throw new InsertionException(message);
        }
    }
}

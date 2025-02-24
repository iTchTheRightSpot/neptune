package org.order;

import lombok.RequiredArgsConstructor;
import org.order.inventory.InventoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    void create(final OrderPayload dto) {}
}

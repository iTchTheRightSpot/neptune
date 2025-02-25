package org.order.inventory;

import io.grpc.Channel;
import lombok.RequiredArgsConstructor;
import org.inventory.InventoryServiceGrpc;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
class InventoryServiceImpl implements InventoryService {

    private final InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    InventoryServiceImpl(final Channel channel) {
        stub = InventoryServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public Optional<Inventory> inventoryByUUID(final UUID uuid) {
        return Optional.empty();
    }

    @Override
    public void deductQty(final UUID productId, final short qty) {
        throw new UnsupportedOperationException("not supported yet implemented.");
    }
}

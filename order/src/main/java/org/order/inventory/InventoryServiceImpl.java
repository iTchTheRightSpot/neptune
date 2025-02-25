package org.order.inventory;

import lombok.RequiredArgsConstructor;
import org.inventory.InventoryProto;
import org.inventory.InventoryServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private final InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    @Override
    public Optional<Inventory> inventoryByUUID(final UUID uuid) {
        final var req = InventoryProto.InventoryRequest.newBuilder().setProductId(uuid.toString()).build();
        try {
            final var resp = stub.emitInventoryDetail(req);
            return Optional.of(new Inventory(resp.getName(), uuid, (short) resp.getQty()));
        } catch (final RuntimeException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public void deductQty(final UUID productId, final short qty) {
        throw new UnsupportedOperationException("not supported yet implemented.");
    }
}
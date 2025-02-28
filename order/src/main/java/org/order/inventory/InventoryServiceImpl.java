package org.order.inventory;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.order.exception.InsertionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import proto.client.InventoryRequest;
import proto.client.InventoryServiceGrpc;
import proto.client.OrderRequest;

import java.util.Optional;
import java.util.UUID;

@Service
class InventoryServiceImpl implements InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    @GrpcClient("inventory-server")
    public void setStub(final InventoryServiceGrpc.InventoryServiceBlockingStub stub) {
        this.stub = stub;
    }

    @Override
    public Optional<Inventory> inventoryByUUID(final UUID uuid) {
        final var req = InventoryRequest.newBuilder().setProductId(uuid.toString()).build();
        try {
            final var resp = stub.emitInventoryDetail(req);
            return Optional.of(new Inventory(resp.getName(), uuid, (short) resp.getQty()));
        } catch (final RuntimeException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void deductQty(final UUID productId, final short qty) {
        final var req = OrderRequest.newBuilder().setProductId(productId.toString()).setQty(qty).build();
        try {
            if (!stub.createOrder(req).getStatus()) throw new InsertionException();
        } catch (final RuntimeException e) {
            log.error(e.getMessage());
            throw new InsertionException(e.getMessage());
        }
    }
}
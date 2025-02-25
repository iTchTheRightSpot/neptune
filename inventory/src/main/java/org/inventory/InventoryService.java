package org.inventory;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
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
class InventoryService extends InventoryServiceGrpc.InventoryServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

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

    @Override
    public void emitInventoryDetail(InventoryProto.InventoryRequest req, StreamObserver<InventoryProto.InventoryResponse> emit) {
        try {
            final var inventory = inventoryById(req.getProductId().trim());
            final var build = InventoryProto.InventoryResponse.newBuilder()
                    .setProductId(req.getProductId().trim())
                    .setName(inventory.name().trim())
                    .setQty(inventory.qty())
                    .build();

            // emit response to client
            emit.onNext(build);
            emit.onCompleted();
            logger.info("emitted inventory detail with uuid {}", req.getProductId().trim());
        } catch (final BadRequestException | NotFoundException e) {
            logger.error("error emitting inventory detail {}", e.getMessage());
            final var status = e instanceof NotFoundException ? Status.NOT_FOUND : Status.UNKNOWN;
            emit.onError(status.withDescription(e.getMessage()).withCause(e.getCause()).asRuntimeException());
        }
    }
}
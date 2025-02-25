package org.inventory;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.inventory.exception.BadRequestException;
import org.inventory.exception.InsertionException;
import org.inventory.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

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

    private static final BiFunction<InventoryStore, String, Inventory> inventoryById = (store, productId) -> {
        final UUID uuid;
        try {
            uuid = UUID.fromString(productId.trim());
        } catch (final IllegalArgumentException e) {
            throw new BadRequestException();
        }
        return store.inventoryByUUID(uuid).orElseThrow(NotFoundException::new);
    };

    InventoryResponse inventoryById(final String productId) {
        final var inv = inventoryById.apply(store, productId.trim());
        return new InventoryResponse(inv.uuid().toString(), inv.name(), inv.qty());
    }

    @Override
    public void emitInventoryDetail(InventoryProto.InventoryRequest req, StreamObserver<InventoryProto.InventoryResponse> emit) {
        try {
            final var inventory = inventoryById.apply(store, req.getProductId().trim());
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
            final var status = e instanceof NotFoundException ? Status.NOT_FOUND : Status.fromCodeValue(400);
            emit.onError(status.withDescription(e.getMessage()).withCause(e.getCause()).asRuntimeException());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(InventoryProto.OrderRequest req, StreamObserver<InventoryProto.OrderResponse> emit) {
        try {
            final var inventory = inventoryById.apply(store, req.getProductId().trim());
            final short qty = (short) (inventory.qty() - (short) req.getQty());
            store.updateQty(inventory.inventoryId(), qty);

            // emit response to client
            emit.onNext(InventoryProto.OrderResponse.newBuilder().setStatus(true).build());
            emit.onCompleted();
        } catch (final BadRequestException | NotFoundException | DataIntegrityViolationException e) {
            logger.error("error creating order {}", e.getMessage());
            final var status = e instanceof NotFoundException ? Status.NOT_FOUND
                    : (e instanceof BadRequestException ? Status.fromCodeValue(400) : Status.fromCodeValue(409));
            emit.onError(status.withDescription(e.getMessage()).withCause(e.getCause()).asRuntimeException());
        }
    }
}
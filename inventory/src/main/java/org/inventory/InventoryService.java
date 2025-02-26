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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import proto.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
class InventoryService extends InventoryServiceGrpc.InventoryServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryStore store;
    private final TransactionTemplate template;

    List<InventoryResponsePayload> all() {
        final List<InventoryResponsePayload> list = new ArrayList<>();
        store.findAll().forEach(a -> list.add(new InventoryResponsePayload(a.uuid().toString(), a.name(), a.qty())));
        return list;
    }

    void create(final InventoryRequestPayload dto) {
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

    InventoryResponsePayload inventoryById(final String productId) {
        final var inv = inventoryById.apply(store, productId.trim());
        return new InventoryResponsePayload(inv.uuid().toString(), inv.name(), inv.qty());
    }

    @Override
    public void emitInventoryDetail(InventoryRequest req, StreamObserver<InventoryResponse> emit) {
        try {
            final var inventory = inventoryById.apply(store, req.getProductId().trim());
            final var build = InventoryResponse.newBuilder()
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
    public void createOrder(OrderRequest req, StreamObserver<OrderResponse> emit) {
        template.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus txstatus) {
                try {
                    final var inventory = inventoryById.apply(store, req.getProductId().trim());
                    final short qty = (short) (inventory.qty() - (short) req.getQty());
                    store.updateQty(inventory.inventoryId(), qty);

                    // emit response to client
                    emit.onNext(OrderResponse.newBuilder().setStatus(true).build());
                    emit.onCompleted();

                    txstatus.flush();
                } catch (final BadRequestException | NotFoundException | DataIntegrityViolationException e) {
                    logger.error("error creating order {}", e.getMessage());
                    txstatus.setRollbackOnly();
                    final var status = e instanceof NotFoundException ? Status.NOT_FOUND
                            : (e instanceof BadRequestException ? Status.fromCodeValue(400) : Status.fromCodeValue(409));
                    emit.onError(status.withDescription(e.getMessage()).withCause(e.getCause()).asRuntimeException());
                }
            }
        });
    }
}
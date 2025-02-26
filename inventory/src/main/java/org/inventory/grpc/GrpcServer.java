package org.inventory.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.inventory.Inventory;
import org.inventory.InventoryStore;
import org.inventory.exception.BadRequestException;
import org.inventory.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import proto.server.*;

import java.util.UUID;
import java.util.function.BiFunction;

@Service
@GrpcService
@RequiredArgsConstructor
class GrpcServer extends InventoryServiceGrpc.InventoryServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(GrpcServer.class);

    private final InventoryStore store;
    private final TransactionTemplate template;

    private static final BiFunction<InventoryStore, String, Inventory> inventoryById = (store, productId) -> {
        final UUID uuid;
        try {
            uuid = UUID.fromString(productId.trim());
        } catch (final IllegalArgumentException e) {
            throw new BadRequestException();
        }
        return store.inventoryByUUID(uuid).orElseThrow(NotFoundException::new);
    };

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
            log.info("emitted inventory detail with uuid {}", req.getProductId().trim());
        } catch (final BadRequestException | NotFoundException e) {
            log.error("error emitting inventory detail {}", e.getMessage());
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
                    log.error("error creating order {}", e.getMessage());
                    txstatus.setRollbackOnly();
                    final StatusRuntimeException s;
                    if (e instanceof DataIntegrityViolationException) {
                        s = Status.fromCodeValue(400).withDescription("quantity to deduct is greater than inventory").withCause(e.getCause()).asRuntimeException();
                    } else if (e instanceof BadRequestException) {
                        s = Status.fromCodeValue(409).withDescription(e.getMessage()).withCause(e.getCause()).asRuntimeException();
                    } else {
                        s = Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e.getCause()).asRuntimeException();
                    }

                    emit.onError(s);
                }
            }
        });
    }
}

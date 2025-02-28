package org.inventory.grpc;

import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.services.HealthStatusManager;
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

import static io.grpc.Status.NOT_FOUND;
import static io.grpc.Status.fromCodeValue;

@Service
@GrpcService
@RequiredArgsConstructor
class GrpcServer extends InventoryServiceGrpc.InventoryServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(GrpcServer.class);

    private final InventoryStore store;
    private final TransactionTemplate template;

    private static final BiFunction<InventoryStore, String, Inventory> inventoryByUUID = (store, productId) -> {
        final UUID uuid;
        try {
            uuid = UUID.fromString(productId.trim());
        } catch (final IllegalArgumentException e) {
            throw new BadRequestException();
        }
        return store.inventoryByUUID(uuid).orElseThrow(NotFoundException::new);
    };
    private final HealthStatusManager healthStatusManager;


    /** Emits the details for a specific {@link Inventory} based on the provided UUID. */
    @Override
    public void emitInventoryDetail(InventoryRequest req, StreamObserver<InventoryResponse> emit) {
        try {
            final var inventory = inventoryByUUID.apply(store, req.getProductId().trim());
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

            final var err = (e instanceof NotFoundException)
                    ? NOT_FOUND.withDescription(e.getMessage()).withCause(e.getCause()).asRuntimeException()
                    : fromCodeValue(400).withDescription(e.getMessage()).withCause(e.getCause()).asRuntimeException();

            emit.onError(err);
        }
    }

    @Override
    public void createOrder(OrderRequest req, StreamObserver<OrderResponse> emit) {
        // manually handling transaction due to metadata WARNING
        template.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus txstatus) {
                try {
                    final var inventory = inventoryByUUID.apply(store, req.getProductId().trim());
                    final short qty = (short) (inventory.qty() - (short) req.getQty());
                    store.updateQty(inventory.inventoryId(), qty);

                    txstatus.flush();

                    // emit response to client
                    emit.onNext(OrderResponse.newBuilder().setStatus(true).build());
                    emit.onCompleted();
                } catch (final BadRequestException | NotFoundException | DataIntegrityViolationException e) {
                    log.error("error creating order {}", e.getMessage());
                    txstatus.setRollbackOnly();

                    final var err = e instanceof DataIntegrityViolationException
                            ? fromCodeValue(400).withDescription("qty to deduct is greater than inventory qty").withCause(e.getCause()).asRuntimeException() :
                            e instanceof BadRequestException
                                    ? fromCodeValue(409).withDescription(e.getMessage()).withCause(e.getCause()).asRuntimeException()
                                    : NOT_FOUND.withDescription(e.getMessage()).withCause(e.getCause()).asRuntimeException();

                    emit.onError(err);
                }
            }
        });
    }
}

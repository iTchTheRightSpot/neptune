package org.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.order.exception.BadRequestException;
import org.order.exception.InsertionException;
import org.order.exception.NotFoundException;
import org.order.inventory.Inventory;
import org.order.inventory.InventoryService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.order.OrderStatus.CONFIRMED;

@ExtendWith({ MockitoExtension.class, SpringExtension.class })
@ActiveProfiles("test")
final class OrderServiceTest {

    private OrderService orderService;
    @Mock
    private OrderStore store;
    @Mock
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(store, inventoryService);
    }

    @Test
    void rejectCreation_ThrowNotFound_ProductIdIsNotUUID() {
        // given
        final var payload = OrderPayload.builder().productId("uuid").build();

        // method to test & assert
        assertThatThrownBy(() -> orderService.create(payload)) //
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("invalid product id");

        verify(inventoryService, times(0)).inventoryByUUID(any(UUID.class));
    }


    @Test
    void rejectCreation_ThrowNotFound_ProductNotFound() {
        // given
        final var payload = OrderPayload.builder().productId(UUID.randomUUID().toString()).build();

        // when
        when(inventoryService.inventoryByUUID(any(UUID.class))).thenReturn(Optional.empty());

        // method to test & assert
        assertThatThrownBy(() -> orderService.create(payload)) //
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("invalid product id");

        verify(inventoryService, times(1)).inventoryByUUID(any(UUID.class));
    }

    @Test
    void rejectCreation_ThrowBadRequest_InventoryLessThanRequestQty() {
        // given
        final var payload = new OrderPayload(UUID.randomUUID().toString(), (short) 10, CONFIRMED);
        final var inventory = Optional.of(Inventory.builder().qty((short) 2).build());

        // when
        when(inventoryService.inventoryByUUID(any(UUID.class))).thenReturn(inventory);

        // method to test & assert
        assertThatThrownBy(() -> orderService.create(payload)) //
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("qty greater than inventory");

        verify(store, times(0)).save(any(Order.class));
    }

    @Test
    void rejectCreation_ThrowInsertion_InventoryOutOfStock() {
        // given
        final short qty = 2;
        final var payload = new OrderPayload(UUID.randomUUID().toString(), qty, CONFIRMED);
        final var inventory = Optional.of(Inventory.builder().qty(qty).build());

        // when
        when(inventoryService.inventoryByUUID(any(UUID.class))).thenReturn(inventory);
        when(store.save(any(Order.class))).thenReturn(new Order());
        doThrow(InsertionException.class).when(inventoryService).deductQty(any(UUID.class), anyShort());

        // method to test & assert
        assertThatThrownBy(() -> orderService.create(payload)) //
                .isInstanceOf(InsertionException.class)
                .hasMessageContaining("error saving data");

        verify(inventoryService, times(1)).inventoryByUUID(any(UUID.class));
        verify(inventoryService, times(1)).deductQty(any(UUID.class), anyShort());
    }

    @Test
    void success_CreateOrder() {
        // given
        final short qty = 2;
        final var payload = new OrderPayload(UUID.randomUUID().toString(), qty, CONFIRMED);
        final var inventory = Optional.of(Inventory.builder().qty(qty).build());

        // when
        when(inventoryService.inventoryByUUID(any(UUID.class))).thenReturn(inventory);
        when(store.save(any(Order.class))).thenReturn(new Order());
        doNothing().when(inventoryService).deductQty(any(UUID.class), anyShort());

        // method to test
        orderService.create(payload);

        // assert
        verify(inventoryService, times(1)).inventoryByUUID(any(UUID.class));
        verify(inventoryService, times(1)).deductQty(any(UUID.class), anyShort());
    }
}
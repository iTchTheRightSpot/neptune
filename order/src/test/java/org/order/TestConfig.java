package org.order;

import org.order.exception.InsertionException;
import org.order.inventory.Inventory;
import org.order.inventory.InventoryService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Optional;
import java.util.UUID;

@TestConfiguration
class TestConfig {

    static class InvServImpl implements InventoryService {
        @Override
        public Optional<Inventory> inventoryByUUID(final UUID uuid) {
            final var id = UUID.randomUUID();
            final short qty = 10;
            return Optional.of(new Inventory(id.toString(), id, qty));
        }

        @Override
        public void deductQty(final UUID productId, final short qty) {
            if (qty < 0) throw new InsertionException("simulate error for transaction behaviour");
        }
    }

    @Bean
    @Primary
    InventoryService service() {
        return new InvServImpl();
    }
}

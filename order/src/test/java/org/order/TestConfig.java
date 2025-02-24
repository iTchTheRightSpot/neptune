package org.order;

import org.order.inventory.Inventory;
import org.order.inventory.InventoryService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.UUID;

@TestConfiguration
class TestConfig {

    static class InvServImpl implements InventoryService {
        @Override
        public Inventory inventoryByUUID(final UUID uuid) {
            final var id = UUID.randomUUID();
            final short qty = 10;
            return new Inventory(id.toString(), id, qty);
        }
    }

    @Bean
    @Primary
    InventoryService service() {
        return new InvServImpl();
    }
}

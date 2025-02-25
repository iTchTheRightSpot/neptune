package org.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InventoryStoreTest extends TestAbstract {

    @Autowired
    private InventoryStore store;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        final var uuid = UUID.randomUUID();
        inventory = store.save(new Inventory(uuid, uuid.toString(), (short) 10));
    }

    @Test
    void success_givenInventory_shouldUpdateQty() {
        // method to test
        store.updateQty(inventory.inventoryId(), (short) 4);

        // assert
        final var inv = store.inventoryByUUID(inventory.uuid());

        assertThat(inv.isPresent()).isTrue();
        assertThat(inv.get().qty()).isEqualTo((short) 4);
    }

    @Test
    void fail_shouldValidateConstraint_inventoryCannotBeNegative() {
        // given
        final short qty = (short) (inventory.qty() - (short) 11);

        // method to test & assert
        assertThatThrownBy(() -> store.updateQty(inventory.inventoryId(), qty)) //
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
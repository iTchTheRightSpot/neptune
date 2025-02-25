package org.inventory;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

interface InventoryStore extends CrudRepository<Inventory, Integer> {
    @Query("SELECT * FROM inventory WHERE uuid = :id")
    Optional<Inventory> inventoryByUUID(@Param("id") final UUID uuid);

    @Transactional
    @Modifying
    @Query("UPDATE inventory SET qty = :qty WHERE inventory_id = :id")
    void updateQty(@Param("id") final int inventoryId, @Param("qty") final short qty);
}
package org.inventory;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface InventoryStore extends CrudRepository<Inventory, Integer> {
    @Query("SELECT * FROM inventory WHERE uuid = :id")
    Optional<Inventory> inventoryByUUID(@Param("id") final String uuid);
}
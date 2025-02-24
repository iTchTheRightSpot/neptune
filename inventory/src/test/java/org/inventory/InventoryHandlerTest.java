package org.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class InventoryHandlerTest extends AbstractIntegration {
    @Value("/${route.prefix}inventory")
    private String prefix;

    @Autowired
    private InventoryStore store;

    private Inventory inventory;
    private final short qty = 10;

    @BeforeEach
    void setUp() {
        final var uuid = UUID.randomUUID();
        inventory = store.save(new Inventory(uuid, uuid.toString(), qty));
    }

    @Test
    void shouldReturnAllInventories() throws Exception {
        super.mockmvc.perform(get(prefix + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("*").isNotEmpty())
                .andExpect(jsonPath("*").isArray())
                .andExpect(jsonPath("$[*]", hasSize(3)))
                .andExpect(jsonPath("$[*].product_id", everyItem(not(emptyOrNullString()))))
                .andExpect(jsonPath("$[*].name", everyItem(not(emptyOrNullString()))))
                .andExpect(jsonPath("$[*].qty", everyItem(greaterThanOrEqualTo(0))));
    }

    @Test
    void shouldCreateInventory() throws Exception {
        final String content = super.mapper.writeValueAsString(new InventoryPayload("uuid", qty));
        super.mockmvc.perform(post(prefix).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnInventoryByUUID() throws Exception {
        super.mockmvc.perform(get(prefix + "/" + inventory.uuid().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.product_id", is(inventory.uuid().toString())))
                .andExpect(jsonPath("$.name", is(inventory.name())))
                .andExpect(jsonPath("$.qty", is((int) inventory.qty())));
    }

    @Test
    void shouldReturnError_InvalidInventoryByUUID() throws Exception {
        super.mockmvc.perform(get(prefix + "/invalid")).andExpect(status().isBadRequest());
    }

}
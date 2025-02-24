package org.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.util.Random;
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

    private Inventory inv1;

    @BeforeEach
    void setUp() {
        final var uuid = UUID.randomUUID();
        inv1 = store.save(new Inventory(uuid, uuid.toString(), new Random().nextInt()));

        final var uuid1 = UUID.randomUUID();
        store.save(new Inventory(uuid1, uuid1.toString(), new Random().nextInt() + 10));
    }

    @Test
    void shouldReturnAllInventories() throws Exception {
        super.mockmvc.perform(get(prefix))
                .andExpect(status().isOk())
                .andExpect(jsonPath("*").isNotEmpty())
                .andExpect(jsonPath("*").isArray())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[*].uuid", everyItem(not(emptyOrNullString()))))
                .andExpect(jsonPath("$[*].name", everyItem(not(emptyOrNullString()))))
                .andExpect(jsonPath("$[*].qty", everyItem(greaterThanOrEqualTo(0))));
    }

    @Test
    void shouldCreateInventory() throws Exception {
        final String content = super.mapper.writeValueAsString(new InventoryPayload("uuid", new Random().nextInt()));
        super.mockmvc.perform(post(prefix).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnInventoryByUUID() throws Exception {
        super.mockmvc.perform(get(URI.create(prefix + inv1.uuid().toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.uuid", is(inv1.uuid().toString())))
                .andExpect(jsonPath("$.name", is(inv1.name())))
                .andExpect(jsonPath("$.qty", is(inv1.qty())));
    }

}
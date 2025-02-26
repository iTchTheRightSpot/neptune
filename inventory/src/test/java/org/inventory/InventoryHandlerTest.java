package org.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
final class InventoryHandlerTest extends TestAbstract {

    @Autowired
    private MockMvc mockmvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private InventoryStore store;

    @Value("/${route.prefix}inventory")
    private String prefix;

    private Inventory inventory;
    private final short qty = 10;

    @BeforeEach
    void setUp() {
        final var uuid = UUID.randomUUID();
        inventory = store.save(new Inventory(uuid, uuid.toString(), qty));
        final var uuid1 = UUID.randomUUID();
        store.save(new Inventory(uuid1, uuid1.toString(), qty));
        final var uuid2 = UUID.randomUUID();
        store.save(new Inventory(uuid2, uuid2.toString(), qty));
    }

    @Test
    void shouldReturnAllInventories() throws Exception {
        mockmvc.perform(get(prefix + "/all"))
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
        final String content = mapper.writeValueAsString(new InventoryPayload("uuid", qty));

        mockmvc.perform(post(prefix).contentType(APPLICATION_JSON).content(content)).andExpect(status().isCreated());
    }

    @Test
    void shouldReturnInventoryByUUID() throws Exception {
        mockmvc.perform(get(prefix + "/" + inventory.uuid().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.product_id", is(inventory.uuid().toString())))
                .andExpect(jsonPath("$.name", is(inventory.name())))
                .andExpect(jsonPath("$.qty", is((int) inventory.qty())));
    }

    @Test
    void shouldReturnError_InvalidInventoryByUUID() throws Exception {
        mockmvc.perform(get(prefix + "/invalid")).andExpect(status().isBadRequest());
    }

}
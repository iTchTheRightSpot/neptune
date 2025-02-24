package org.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.order.OrderStatus.CONFIRMED;
import static org.order.OrderStatus.PENDING;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
final class OrderHandlerTest extends AbstractIntegration {

    @Value("/${route.prefix}order")
    private String prefix;

    private final short qty = 10;

    @Autowired
    private OrderStore store;

    @BeforeEach
    void setUp() {
        final var uuid = UUID.randomUUID();
        store.save(new Order(uuid, uuid, qty, CONFIRMED));
    }

    @Test
    void shouldReturnAllOrders() throws Exception {
        super.mockmvc.perform(get(prefix))
                .andExpect(status().isOk())
                .andExpect(jsonPath("*").isNotEmpty())
                .andExpect(jsonPath("*").isArray())
                .andExpect(jsonPath("$[*]", hasSize(1)))
                .andExpect(jsonPath("$[*].order_id", everyItem(not(emptyOrNullString()))))
                .andExpect(jsonPath("$[*].status", everyItem(not(emptyOrNullString()))))
                .andExpect(jsonPath("$[*].qty", everyItem(greaterThanOrEqualTo((int) qty))));
    }

    @Test
    void shouldCreateOrder() throws Exception {
        final String content = super.mapper.writeValueAsString(new OrderPayload("uuid", qty, PENDING));
        super.mockmvc.perform(post(prefix).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isCreated());
    }

}
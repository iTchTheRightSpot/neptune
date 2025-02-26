package org.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.order.OrderStatus.CONFIRMED;
import static org.order.OrderStatus.PENDING;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
class OrderHandlerTest {

    @Autowired
    protected MockMvc mockmvc;
    @Autowired
    protected ObjectMapper mapper;
    @Autowired
    private OrderStore store;

    @Value("/${route.prefix}order")
    private String prefix;

    private final short qty = 5;

    @BeforeEach
    void setUp() {
        final var uuid = UUID.randomUUID();
        store.save(new Order(uuid, uuid, qty, CONFIRMED));
    }

    @Test
    void shouldReturnAllOrders() throws Exception {
        mockmvc.perform(get(prefix))
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
        final String content = mapper.writeValueAsString(new OrderRequestPayload(UUID.randomUUID().toString(), qty, PENDING));
        mockmvc.perform(post(prefix).contentType(APPLICATION_JSON).content(content)).andExpect(status().isCreated());
    }

}
package org.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
record OrderRequestPayload(
        @JsonProperty("product_id")
        @NotNull
        @NotEmpty
        String productId,
        @NotNull
        Short qty,
        @NotNull
        OrderStatus status
) {
}

record OrderResponsePayload(@JsonProperty("order_id") String orderId, @JsonProperty("product_id") String productId, short qty, OrderStatus status) {}
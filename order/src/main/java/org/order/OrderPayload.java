package org.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
record OrderPayload(
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

record OrderResponse(@JsonProperty("order_id") String orderId, short qty, OrderStatus status) {}
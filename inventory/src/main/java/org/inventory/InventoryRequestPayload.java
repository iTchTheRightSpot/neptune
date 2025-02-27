package org.inventory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
record InventoryRequestPayload(
        @NotNull
        @NotEmpty
        @Size.List({@Size(min = 1), @Size(max = 50)})
        String name,
        @NotNull
        Short qty
) {}

record InventoryResponsePayload(
        @JsonProperty("product_id")
        String productId,
        String name,
        short qty
) {}
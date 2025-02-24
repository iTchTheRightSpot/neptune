package org.order.inventory;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record Inventory(String name, @JsonProperty("product_id") UUID productId, short qty) {
}

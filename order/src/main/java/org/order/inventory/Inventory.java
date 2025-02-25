package org.order.inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record Inventory(String name, @JsonProperty("product_id") UUID productId, short qty) {}
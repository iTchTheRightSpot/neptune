package org.inventory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "${route.prefix}inventory")
@RequiredArgsConstructor
class InventoryHandler {

    private final InventoryService service;

    @ResponseStatus(OK)
    @GetMapping(path = "/all", produces = APPLICATION_JSON_VALUE)
    List<InventoryResponsePayload> all() {
        return service.all();
    }

    @ResponseStatus(OK)
    @GetMapping(path = "/{product_id}", produces = APPLICATION_JSON_VALUE)
    InventoryResponsePayload inventoryById(@PathVariable("product_id") final String productId) {
        return service.inventoryById(productId);
    }

    @ResponseStatus(CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    void create(@Valid @RequestBody final InventoryRequestPayload dto) {
        service.create(dto);
    }
}
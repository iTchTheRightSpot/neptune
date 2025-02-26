package org.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "${route.prefix}order")
@RequiredArgsConstructor
class OrderHandler {

    private final OrderService service;

    @ResponseStatus(OK)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    List<OrderResponsePayload> all() {
        return service.all();
    }

    @ResponseStatus(CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    void create(@Valid @RequestBody final OrderRequestPayload dto) {
        service.create(dto);
    }
}

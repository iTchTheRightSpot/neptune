package org.inventory.exception;

import org.springframework.http.HttpStatus;

public final class NotFoundException extends AbstractException {
    public NotFoundException() {
        super(HttpStatus.NOT_FOUND, "Not found");
    }
}

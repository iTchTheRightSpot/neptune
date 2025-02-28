package org.inventory.exception;

import org.springframework.http.HttpStatus;

public final class BadRequestException extends AbstractException {
    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST, "bad request");
    }
}

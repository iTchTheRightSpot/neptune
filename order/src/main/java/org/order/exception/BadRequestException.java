package org.order.exception;

import org.springframework.http.HttpStatus;

public final class BadRequestException extends AbstractException {
    public BadRequestException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}

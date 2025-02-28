package org.order.exception;

import org.springframework.http.HttpStatus;

abstract class AbstractException extends RuntimeException {
    private final HttpStatus status;

    public AbstractException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }
}
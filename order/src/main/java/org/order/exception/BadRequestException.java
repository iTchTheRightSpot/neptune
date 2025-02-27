package org.order.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException() {
        super("bad request");
    }

    public BadRequestException(final String message) {
        super(message);
    }
}

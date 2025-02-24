package org.order.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("not found");
    }

    public NotFoundException(final String message) {
        super(message);
    }
}

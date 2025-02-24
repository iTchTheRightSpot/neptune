package org.order.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException() {
        super("bad request");
    }
}

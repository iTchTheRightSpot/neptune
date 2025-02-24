package org.order.exception;

public class InsertionException extends RuntimeException {
    public InsertionException() {
        super("error saving data");
    }

    public InsertionException(final String message) {
        super(message);
    }
}

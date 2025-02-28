package org.order.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

public class InsertionException extends AbstractException {
    public InsertionException() {
        super(CONFLICT, "error saving data");
    }

    public InsertionException(final String message) {
        super(CONFLICT, message);
    }
}

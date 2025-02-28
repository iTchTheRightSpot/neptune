package org.inventory.exception;

import org.springframework.http.HttpStatus;

public final class InsertionException extends AbstractException {
    public InsertionException() {
        super(HttpStatus.CONFLICT, "error saving insertion");
    }
}

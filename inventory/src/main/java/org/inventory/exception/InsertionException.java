package org.inventory.exception;

public class InsertionException extends RuntimeException {
    public InsertionException() {
        super("error saving insertion");
    }
}

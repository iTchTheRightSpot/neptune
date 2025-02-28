package org.order.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AbstractException {
    public NotFoundException(final String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}

package org.inventory.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.function.BiFunction;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ControllerAdvices {

    private static final Logger log = LoggerFactory.getLogger(ControllerAdvices.class);

    public record ExceptionResponse(String message, HttpStatus status) {}

    private static final BiFunction<Class<?>, String, String> FORMAT =
            (clazz, message) -> message.replace(clazz.getName() + ": ", "");

    @ExceptionHandler(InsertionException.class)
    public ResponseEntity<ExceptionResponse> insertionException(final InsertionException ex) {
        final String message = ex.getCause() != null ? FORMAT.apply(ex.getCause().getClass(), ex.getMessage()) : ex.getMessage();
        return new ResponseEntity<>(new ExceptionResponse(message, HttpStatus.CONFLICT), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> notfoundException(final NotFoundException ex) {
        final String message = ex.getCause() != null ? FORMAT.apply(ex.getCause().getClass(), ex.getMessage()) : ex.getMessage();
        return new ResponseEntity<>(new ExceptionResponse(message, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> badRequest(final BadRequestException ex) {
        final String message = ex.getCause() != null ? FORMAT.apply(ex.getCause().getClass(), ex.getMessage()) : ex.getMessage();
        return new ResponseEntity<>(new ExceptionResponse(message, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentException(final MethodArgumentNotValidException ex) {
        final String message = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        return new ResponseEntity<>(new ExceptionResponse(message, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> missingRequestParameterException(final MissingServletRequestParameterException ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage(), BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionResponse> handlerMethodException(final HandlerMethodValidationException ex) {
        final String message = ex.getParameterValidationResults().getFirst().getResolvableErrors().getFirst().getDefaultMessage();
        return new ResponseEntity<>(new ExceptionResponse(message, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> requestMethodException(final HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage(), BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponse> noHandlerFoundException(final NoResourceFoundException ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage(), BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> runtimeException(final RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new ExceptionResponse("checked exception error", HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

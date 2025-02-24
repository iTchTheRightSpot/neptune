package org.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootApplication
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	public record ExceptionResponse(String message, HttpStatus status) {}

	// Class is responsible for handling the throwable error for the given request and response exchange.
	// https://stackoverflow.com/questions/66254535/spring-cloud-gateway-global-exception-handling-and-custom-error-response
	// In this example, we set the order of our global error handler to -2. This is to give it a higher
	// priority than the DefaultErrorWebExceptionHandler, which is registered at @Order(-1). link https://www.baeldung.com/spring-webflux-errors
	@Component
	@Order(-2)
	static class GatewayErrorHandler extends AbstractErrorWebExceptionHandler {
		public GatewayErrorHandler(final ErrorAttributes attributes, final ApplicationContext context, final ServerCodecConfigurer configurer) {
			super(attributes, new WebProperties.Resources(), context);
			super.setMessageWriters(configurer.getWriters());
			super.setMessageReaders(configurer.getReaders());
		}

		// handles & responds to errors with gateway.
		@Override
		protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes attributes) {
			final HandlerFunction<ServerResponse> func = request -> {
				final var map = attributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
				final var status = HttpStatus.valueOf((Integer) map.get("status"));
				final ExceptionResponse body;
				if (status.name().equals(HttpStatus.NOT_FOUND.name())) {
					body = new ExceptionResponse("invalid request path", status);
				} else {
					body = new ExceptionResponse((String) map.get("error"), status);
				}
				return ServerResponse.status(status).contentType(APPLICATION_JSON).body(BodyInserters.fromValue(body));
			};
			return RouterFunctions.route(RequestPredicates.all(), func);
		}
	}

}

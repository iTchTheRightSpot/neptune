package org.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public RouteLocator myRoutes(final RouteLocatorBuilder builder, final Environment env) {
		final int order = env.getProperty("ORDER_PORT", Integer.class, 1998);
		final int inventory = env.getProperty("INVENTORY_PORT", Integer.class, 1997);
		return builder.routes()
				.route(p -> p.path("/api/v1/order/**").uri("http://localhost:" + order + "/api/v1/order"))
				.route(p -> p.path("/api/v1/inventory/**").uri("http://localhost:" + inventory + "/api/v1/inventory"))
				.route(p -> p.path("/get").filters(f -> f.addRequestHeader("Hello", "World")).uri("http://httpbin.org:80"))
				.build();
	}

}

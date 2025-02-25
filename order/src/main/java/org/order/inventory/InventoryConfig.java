package org.order.inventory;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import org.inventory.InventoryServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
class InventoryConfig {

    @Bean
    InventoryServiceGrpc.InventoryServiceBlockingStub blockingInventoryClient(final Environment env) {
        final String port = env.getProperty("grpc.inventory.port");
        final var channel = Grpc.newChannelBuilder(port, InsecureChannelCredentials.create()).build();
        return InventoryServiceGrpc.newBlockingStub(channel);
    }

}
package org.order.inventory;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import proto.client.InventoryServiceGrpc;

@Configuration
class InventoryConfig {

    @Bean
    InventoryServiceGrpc.InventoryServiceBlockingStub stub(final Environment env) {
        final int port = env.getProperty("grpc.inventory.port", Integer.class, 2022);
        final var channel = Grpc.newChannelBuilder("localhost:" + port, InsecureChannelCredentials.create()).build();
        return InventoryServiceGrpc.newBlockingStub(channel);
    }

}
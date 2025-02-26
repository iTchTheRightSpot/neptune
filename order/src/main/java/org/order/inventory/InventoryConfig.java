package org.order.inventory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.grpc.client.GrpcChannelFactory;
import proto.client.InventoryServiceGrpc;

@Configuration
class InventoryConfig {

    @Bean
    InventoryServiceGrpc.InventoryServiceBlockingStub stub(final Environment env, final GrpcChannelFactory channels) {
        final String port = "0.0.0.0:" + env.getProperty("grpc.inventory.port", Integer.class, 9090);
//        final var channel = Grpc.newChannelBuilder("localhost:" + port, InsecureChannelCredentials.create()).build();
        final var channel = channels.createChannel(port);
        return InventoryServiceGrpc.newBlockingStub(channel);
    }

}
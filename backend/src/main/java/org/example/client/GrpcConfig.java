package org.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.grpc.CarritoServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel canal(@Value("${servidor.grpc.host:localhost}") String host,
                                @Value("${servidor.grpc.puerto:6565}") int puerto) {
        return ManagedChannelBuilder.forAddress(host, puerto).usePlaintext().build();
    }

    @Bean
    public CarritoServiceGrpc.CarritoServiceBlockingStub carritoStub(ManagedChannel canal) {
        return CarritoServiceGrpc.newBlockingStub(canal);
    }
}

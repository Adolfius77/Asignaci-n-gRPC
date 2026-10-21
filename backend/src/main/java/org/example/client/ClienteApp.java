package org.example.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class ClienteApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ClienteApp.class)
                .properties("server.port=8080", "grpc.enabled=false")
                .run(args);
    }
}

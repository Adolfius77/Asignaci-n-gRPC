package org.example.server;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class ServidorApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServidorApp.class)
                .web(WebApplicationType.NONE)
                .properties("grpc.port=6565")
                .run(args);
    }
}

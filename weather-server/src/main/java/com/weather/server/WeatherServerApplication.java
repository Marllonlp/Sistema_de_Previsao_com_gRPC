package com.weather.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.grpc.Server;
import io.grpc.ServerBuilder;

@SpringBootApplication
public class WeatherServerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WeatherServerApplication.class, args);

        Server server = ServerBuilder.forPort(9090)
                .addService(new WeatherServiceImpl())
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.awaitTermination();
    }
}

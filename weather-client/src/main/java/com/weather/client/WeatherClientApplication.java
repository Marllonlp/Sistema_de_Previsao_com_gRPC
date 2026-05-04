package com.weather.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

@SpringBootApplication
public class WeatherClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherClientApplication.class, args);
    }

    @Bean
    public Channel grpcChannel() {
        return ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();
    }
}

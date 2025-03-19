package org.example.roamingaggregatorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class RoamingAggregatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoamingAggregatorServiceApplication.class, args);
    }

}

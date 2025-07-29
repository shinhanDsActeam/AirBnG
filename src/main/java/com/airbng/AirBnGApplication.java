package com.airbng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
public class AirBnGApplication {
    public static void main(String[] args) {
        SpringApplication.run(AirBnGApplication.class, args);
    }
}

package org.example.moneylog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MoneylogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneylogApplication.class, args);
    }

}

package com.shop.shopping_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ShoppingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingPlatformApplication.class, args);
    }
}
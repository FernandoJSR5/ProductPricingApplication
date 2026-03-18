package com.commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ProductPricingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductPricingApplication.class, args);
    }
}

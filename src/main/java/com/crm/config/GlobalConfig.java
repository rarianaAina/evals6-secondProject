package com.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class GlobalConfig {
    @Value("${api.laravel.url}")
    private String laravelApiUrl;

    @Value("${global.discount.rate}")
    private Double globalDiscountRate;
}
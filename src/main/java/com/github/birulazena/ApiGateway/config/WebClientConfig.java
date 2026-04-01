package com.github.birulazena.ApiGateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient userClient(@Value("${user-service.url}") String url) {
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }

    @Bean
    WebClient authClient(@Value("${auth-service.url}") String url) {
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }
}

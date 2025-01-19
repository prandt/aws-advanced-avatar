package br.pucpr.authserver

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun gravatarClient(): WebClient = WebClient
        .builder()
        .baseUrl("https://api.gravatar.com/v3")
        .defaultHeader("Authorization", "Bearer ")
        .build()


}
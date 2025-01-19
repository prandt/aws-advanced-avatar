package br.pucpr.authserver.storage

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AmazonS3Config {

    @Bean
    fun amazonS3(): AmazonS3 {
        return AmazonS3ClientBuilder.standard()
            .withRegion(Regions.SA_EAST_1)
            .withCredentials(EnvironmentVariableCredentialsProvider())
            .build()
    }
}
package io.github.gprindevelopment.recommender.discogs

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class DiscogsClientConfig {

    @Bean
    fun requestInterceptor(@Value("\${discogs.apiKey}") apiKey: String,
                           @Value("\${discogs.apiSecret}") apiSecret: String): RequestInterceptor {
        return RequestInterceptor { template: RequestTemplate ->
            template.query("key", apiKey)
            template.query("secret", apiSecret)
        }
    }
}
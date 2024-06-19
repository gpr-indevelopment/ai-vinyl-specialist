package io.github.gprindevelopment.recommender

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class AiVinylRecommenderApplication

fun main(args: Array<String>) {
	runApplication<AiVinylRecommenderApplication>(*args)
}

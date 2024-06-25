package io.github.gprindevelopment.recommender

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

//TODO: Global extension function for easy random generate random instance
@SpringBootApplication
@EnableFeignClients
class AiVinylRecommenderApplication

fun main(args: Array<String>) {
	runApplication<AiVinylRecommenderApplication>(*args)
}

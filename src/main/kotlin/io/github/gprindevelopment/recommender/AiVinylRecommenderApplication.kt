package io.github.gprindevelopment.recommender

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

//TODO: Global extension function for easy random generate random instance
//TODO: Do git tagging for landmark commits: Discogs username field with llama3, with openAI, and then tooling enabled.
@SpringBootApplication
@EnableFeignClients
class AiVinylRecommenderApplication

fun main(args: Array<String>) {
	runApplication<AiVinylRecommenderApplication>(*args)
}

package io.github.gprindevelopment.recommender

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.ollama.OllamaContainer
import org.testcontainers.utility.DockerImageName
import kotlin.test.assertEquals


@Testcontainers
@SpringBootTest
class VinylRecommenderAssistantTest {

    companion object {
        @Container
        val ollamaContainer: OllamaContainer = OllamaContainer(
            DockerImageName.parse("langchain4j/ollama-llama3:latest")
                .asCompatibleSubstituteFor("ollama/ollama")
        )

        fun containerBaseUrl(): String {
            return String.format("http://%s:%d", ollamaContainer.host, ollamaContainer.firstMappedPort)
        }

        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("langchain4j.ollama.streaming-chat-model.base-url") { containerBaseUrl() }
        }
    }

    @Autowired
    lateinit var assistant:VinylRecommenderAssistant

    @Test
    fun Should_chat_with_llama_3() {
        val input = "Hello! This is a test. Respond with the X character only, and nothing else."
        val outputStream = assistant.chat(input)

        outputStream
            .onNext {}
            .onComplete { response -> assertEquals("X", response.content().text()) }
            .ignoreErrors()
            .start()
    }
}
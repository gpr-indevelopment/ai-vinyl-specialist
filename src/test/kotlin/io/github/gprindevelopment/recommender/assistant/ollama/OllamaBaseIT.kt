package io.github.gprindevelopment.recommender.assistant.ollama

import io.github.gprindevelopment.recommender.assistant.TestContainers
import io.github.gprindevelopment.recommender.assistant.TestContainers.containerBaseUrl
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class OllamaBaseIT {

    companion object {

        val ollamaContainer = TestContainers.ollamaContainer

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("assistant.ollama.baseUrl") { ollamaContainer.containerBaseUrl() }
        }

        @JvmStatic
        @BeforeAll
        fun startContainer() {
            ollamaContainer.start()
        }
    }
}
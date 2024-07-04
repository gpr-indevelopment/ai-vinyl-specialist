package io.github.gprindevelopment.recommender.assistant.ollama

import io.github.gprindevelopment.recommender.assistant.OllamaTestContainer
import io.github.gprindevelopment.recommender.assistant.OllamaTestContainer.containerBaseUrl
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

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("assistant.ollama.baseUrl") { OllamaTestContainer.container.containerBaseUrl() }
        }
    }
}
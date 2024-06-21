package io.github.gprindevelopment.recommender

import dev.langchain4j.service.TokenStream
import org.awaitility.Awaitility
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.ollama.OllamaContainer
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class AssistantIT {

    companion object {
        @Container
        val ollamaContainer: OllamaContainer = OllamaContainer(
            //TODO: Can we make the modelName property come from the properties file?
            //TODO: How to get rid of hanging containers?
            DockerImageName.parse("langchain4j/ollama-llama3:latest")
                .asCompatibleSubstituteFor("ollama/ollama")
        )

        fun containerBaseUrl(): String {
            return String.format("http://%s:%d", ollamaContainer.host, ollamaContainer.firstMappedPort)
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("langchain4j.ollama.streaming-chat-model.base-url") { containerBaseUrl() }
            registry.add("langchain4j.ollama.chat-model.base-url") { containerBaseUrl() }
        }

        fun assertStream(stream: TokenStream, expected: String, timeout: Duration = Duration.ofMinutes(2)) {
            var response: String? = null
            stream
                .onNext {}
                .onComplete { modelResponse -> response = modelResponse.content().text() }
                // A bug in langchain4j-ollama 0.31 causes assertion errors in the onComplete callback to be ignored, even when providing an onError callback
                .ignoreErrors()
                .start()
            Awaitility.with()
                .pollInterval(Duration.ofSeconds(5))
                .and()
                .atMost(timeout)
                .await()
                .untilAsserted {
                    assertEquals(expected, response)
                }
        }

        //TODO: Can we unify the assertion methods?
        fun assertStreamContainsOneOf(stream: TokenStream, expected: List<String>, timeout: Duration = Duration.ofMinutes(2)) {
            var response: String? = null
            stream
                .onNext { }
                .onComplete { modelResponse -> response = modelResponse.content().text() }
                .ignoreErrors()
                .start()
            Awaitility.with()
                .pollInterval(Duration.ofSeconds(5))
                .and()
                .atMost(timeout)
                .await()
                .untilAsserted {
                    assertNotNull(response)
                    assertTrue(expected.any { response!!.contains(it) })
                }
        }
    }
}
package io.github.gprindevelopment.recommender

import dev.langchain4j.service.TokenStream
import org.awaitility.Awaitility.with
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.ollama.OllamaContainer
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.*
import kotlin.test.assertEquals


@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class VinylRecommenderAssistantTest {

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
            with()
                .pollInterval(Duration.ofSeconds(5))
                .and()
                .atMost(timeout)
                .await()
                .untilAsserted {
                assertEquals(expected, response)
            }
        }
    }

    @Autowired
    lateinit var assistant:VinylRecommenderAssistant

    @Test
    fun Should_stream_chat_with_recommender() {
        val input = "Hello! This is a test. Respond with the X character only, and nothing else."
        val outputStream = assistant.chat(input)

        assertStream(outputStream, "X")
    }

    @Test
    fun Should_chat_with_recommender() {
        val input = "Hello! This is a test. Respond with the X character only, and nothing else."
        val response = assistant.chatSync(input)

        assertEquals("X", response)
    }

    @Test
    fun Two_users_should_stream_chat_with_recommender_maintaining_memory() {
        val maryMemoryId = UUID.randomUUID()
        val johnMemoryId = UUID.randomUUID()
        val maryPrompt = """
               Hello! I am Mary, and this is a test. 
               You must respond this message with a single X character only, and nothing else.
               Then, I will send you a new message with the Y character,
               and you must respond with the Z character, and nothing else.
            """
        val johnPrompt = """
               Hello! I am John, and this is a test. 
               You must respond this message with a single A character only, and nothing else.
               Then, I will send you a new message with the B character,
               and you must respond with the C character, and nothing else.
            """
        assertStream(assistant.chat(maryPrompt, maryMemoryId), "X")
        assertStream(assistant.chat(johnPrompt, johnMemoryId), "A")
        assertStream(assistant.chat("Y", maryMemoryId), "Z")
        assertStream(assistant.chat("B", johnMemoryId), "C")
    }
}
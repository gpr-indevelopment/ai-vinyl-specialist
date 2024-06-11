package io.github.gprindevelopment.recommender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.ollama.OllamaContainer
import org.testcontainers.utility.DockerImageName
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
@Testcontainers
class ChatControllerTest {

    companion object {
        @Container
        val ollamaContainer: OllamaContainer = OllamaContainer(
            DockerImageName.parse("langchain4j/ollama-llama3:latest")
                .asCompatibleSubstituteFor("ollama/ollama")
        )

        fun containerBaseUrl(): String {
            return String.format("http://%s:%d", ollamaContainer.host, ollamaContainer.firstMappedPort)
        }
    }

    @InjectMocks
    lateinit var chatController: ChatController

    @Spy
    val languageModelProvider: LanguageModelProvider = LanguageModelProvider("llama3", containerBaseUrl())


    @Test
    fun Should_chat_with_llama_3() {
        val input = "Hello! Please say hello back, and include the word 'Hello' within your response"
        val output = chatController.chat(input)
        assertTrue(output.contains("Hello"))
    }
}
package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.ollama.OllamaStreamingChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class AssistantConfig {

    @Bean
    fun chatMemoryProvider(): ChatMemoryProvider {
        return ChatMemoryProvider { MessageWindowChatMemory.withMaxMessages(10) }
    }

    @Bean
    fun ollamaChatModel(@Value("\${assistant.ollama.baseUrl}") baseUrl: String,
                        @Value("\${assistant.ollama.modelName}") modelName: String,
                        @Value("\${assistant.ollama.timeout}") timeout: Duration): OllamaChatModel {
        return OllamaChatModel
            .builder()
            .baseUrl(baseUrl)
            .modelName(modelName)
            .timeout(timeout)
            .build()
    }

    @Bean
    fun ollamaStreamingChatModel(@Value("\${assistant.ollama.baseUrl}") baseUrl: String,
                                 @Value("\${assistant.ollama.modelName}") modelName: String,
                                 @Value("\${assistant.ollama.timeout}") timeout: Duration): OllamaStreamingChatModel {
        return OllamaStreamingChatModel
            .builder()
            .baseUrl(baseUrl)
            .modelName(modelName)
            .timeout(timeout)
            .build()
    }
}
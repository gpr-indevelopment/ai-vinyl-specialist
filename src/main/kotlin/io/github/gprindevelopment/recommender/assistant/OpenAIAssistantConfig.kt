package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class OpenAIAssistantConfig {

    @Bean
    fun openAiStreamingChatModel(@Value("\${assistant.openai.apiKey}") apiKey: String,
                                 @Value("\${assistant.openai.modelName}") modelName: String,
                                 @Value("\${assistant.openai.timeout}") timeout: Duration,
                                 @Value("\${assistant.openai.logRequests}") logRequests: Boolean
    ): OpenAiStreamingChatModel {
        return OpenAiStreamingChatModel
            .builder()
            .apiKey(apiKey)
            .modelName(modelName)
            .timeout(timeout)
            .logRequests(logRequests)
            .build()
    }

    @Bean
    fun openAiChatModel(@Value("\${assistant.openai.apiKey}") apiKey: String,
                                 @Value("\${assistant.openai.modelName}") modelName: String,
                                 @Value("\${assistant.openai.timeout}") timeout: Duration,
                                 @Value("\${assistant.openai.logRequests}") logRequests: Boolean
    ): OpenAiChatModel {
        return OpenAiChatModel
            .builder()
            .apiKey(apiKey)
            .modelName(modelName)
            .timeout(timeout)
            .logRequests(logRequests)
            .build()
    }
}
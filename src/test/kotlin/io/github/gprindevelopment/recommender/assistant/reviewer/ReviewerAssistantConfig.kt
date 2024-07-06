package io.github.gprindevelopment.recommender.assistant.reviewer

import dev.langchain4j.model.openai.OpenAiChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class ReviewerAssistantConfig {

    @Bean
    fun openAiTestReviewerChatModel(@Value("\${assistant.openai.apiKey}") apiKey: String,
                                    @Value("\${assistant.openai.modelName}") modelName: String,
                                    @Value("\${assistant.openai.timeout}") timeout: Duration,
                                    @Value("\${assistant.openai.logRequests}") logRequests: Boolean): OpenAiChatModel {
        return OpenAiChatModel
            .builder()
            .responseFormat("json_object")
            .apiKey(apiKey)
            .modelName(modelName)
            .timeout(timeout)
            .logRequests(logRequests)
            .logResponses(true)
            .build()
    }
}
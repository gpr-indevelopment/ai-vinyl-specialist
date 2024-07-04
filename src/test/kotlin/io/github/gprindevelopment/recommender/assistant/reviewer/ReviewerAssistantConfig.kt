package io.github.gprindevelopment.recommender.assistant.reviewer

import dev.langchain4j.model.ollama.OllamaChatModel
import io.github.gprindevelopment.recommender.assistant.OllamaTestContainer
import io.github.gprindevelopment.recommender.assistant.OllamaTestContainer.containerBaseUrl
import io.github.gprindevelopment.recommender.assistant.OllamaTestContainer.modelName
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class ReviewerAssistantConfig {

    @Bean
    fun ollamaTestReviewerChatModel(): OllamaChatModel {
        //TODO: Can we avoid clicking start on the container on multiple places? OllamaBaseIT does it once. Maybe we can move to a getter
        val ollamaTestContainer = OllamaTestContainer.container
        ollamaTestContainer.start()
        return OllamaChatModel
            .builder()
            .baseUrl(ollamaTestContainer.containerBaseUrl())
            .modelName(ollamaTestContainer.modelName())
            //TODO: Can we decrease this timeout? Seems long
            .timeout(Duration.ofMinutes(3))
            .build()
    }
}
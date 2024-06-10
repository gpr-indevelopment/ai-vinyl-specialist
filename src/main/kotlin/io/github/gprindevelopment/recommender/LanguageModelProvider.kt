package io.github.gprindevelopment.recommender

import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.ollama.OllamaChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class LanguageModelProvider(
    @Value("\${model.name}") private val modelName: String,
    @Value("\${model.baseUrl}") private val baseUrl: String
) {

    fun getModel(): ChatLanguageModel {
        // TODO: Adjust this timeout, seems long
        return OllamaChatModel.builder().baseUrl(baseUrl)
            .modelName(modelName).timeout(Duration.ofMinutes(5)).build()
    }
}
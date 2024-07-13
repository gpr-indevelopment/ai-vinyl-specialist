package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.service.Result
import org.springframework.stereotype.Component
import java.util.*

@Component
class DiscogsAssistant(
    val assistant: OpenAIVinylRecommenderAssistant
) {

    fun chat(message: String, memoryId: UUID): Result<EnrichedRecommenderResponse> {
        throw NotImplementedError()
    }
}
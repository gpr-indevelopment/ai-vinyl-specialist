package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.service.Result
import io.github.gprindevelopment.recommender.discogs.DiscogsService
import io.github.gprindevelopment.recommender.discogs.EnrichedVinylRecord
import io.github.gprindevelopment.recommender.discogs.SimpleVinylRecord
import org.springframework.stereotype.Component
import java.util.*

@Component
class DiscogsAssistant(
    val recommender: OpenAIVinylRecommenderAssistant,
    val discogsService: DiscogsService
) {

    fun chat(message: String, memoryId: UUID): Result<EnrichedRecommenderResponse> {
        val recResponse = recommender.chatSync(message, memoryId)
        return Result.builder<EnrichedRecommenderResponse>()
            .tokenUsage(recResponse.tokenUsage())
            .content(EnrichedRecommenderResponse(recResponse.content().message, enrich(recResponse.content().recommendations)))
            .build()
    }

    private fun enrich(simpleRecords: List<SimpleVinylRecord>): List<EnrichedVinylRecord> {
        if (simpleRecords.isEmpty()) return emptyList()
        return simpleRecords.map { discogsService.enrichVinylRecord(it) }
    }
}
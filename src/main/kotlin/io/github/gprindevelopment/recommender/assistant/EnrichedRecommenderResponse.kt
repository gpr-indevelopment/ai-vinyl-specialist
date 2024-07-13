package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.service.Result
import io.github.gprindevelopment.recommender.discogs.EnrichedVinylRecord

data class EnrichedRecommenderResponse(
    val message: String,
    val recommendations: List<EnrichedVinylRecord>
)

val Result<EnrichedRecommenderResponse>.message: String
    get() = this.content().message

val Result<EnrichedRecommenderResponse>.recommendations: List<EnrichedVinylRecord>
    get() = this.content().recommendations
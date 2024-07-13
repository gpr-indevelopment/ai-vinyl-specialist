package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.service.Result
import io.github.gprindevelopment.recommender.discogs.SimpleVinylRecord

data class RecommenderResponse(
    val message: String,
    val recommendations: List<SimpleVinylRecord>
)

val Result<RecommenderResponse>.message: String
    get() = this.content().message

val Result<RecommenderResponse>.recommendations: List<SimpleVinylRecord>
    get() = this.content().recommendations
package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.service.Result
import io.github.gprindevelopment.recommender.domain.VinylRecord

data class RecommenderResponse(
    val message: String,
    val recommendations: List<VinylRecord>
)

val Result<RecommenderResponse>.message: String
    get() = this.content().message

val Result<RecommenderResponse>.recommendations: List<VinylRecord>
    get() = this.content().recommendations
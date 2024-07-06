package io.github.gprindevelopment.recommender.assistant

import io.github.gprindevelopment.recommender.domain.VinylRecord

data class RecommenderResponse(
    val message: String,
    val recommendations: List<VinylRecord>
)
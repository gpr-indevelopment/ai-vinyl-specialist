package io.github.gprindevelopment.recommender

import java.util.UUID

data class RecommenderSession(
    val memoryId: UUID,
    val user: DiscogsUser,
    val collection: List<VinylRecord>,
)

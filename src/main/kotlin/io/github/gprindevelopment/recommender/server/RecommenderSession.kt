package io.github.gprindevelopment.recommender.server

import io.github.gprindevelopment.recommender.domain.VinylRecord
import io.github.gprindevelopment.recommender.discogs.DiscogsUser
import java.util.UUID

data class RecommenderSession(
    val memoryId: UUID,
    val user: DiscogsUser,
    val collection: List<VinylRecord>,
)

package io.github.gprindevelopment.recommender.discogs

import java.net.URL

data class EnrichedVinylRecord(
    val title: String,
    val artist: String,
    val releaseId: Int,
    val coverImageUrl: URL? = null
)

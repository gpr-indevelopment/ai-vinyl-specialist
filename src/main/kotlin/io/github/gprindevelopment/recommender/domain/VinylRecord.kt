package io.github.gprindevelopment.recommender.domain

import java.net.URL

data class VinylRecord(val title: String, val artist: String, val coverImage: URL? = null)

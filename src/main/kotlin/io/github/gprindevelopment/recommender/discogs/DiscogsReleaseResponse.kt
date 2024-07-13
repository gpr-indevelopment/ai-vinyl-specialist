package io.github.gprindevelopment.recommender.discogs

import com.fasterxml.jackson.annotation.JsonProperty

data class DiscogsReleaseResponse(
    val artists: List<Artist>,
    val notes: String,
    val genres: List<String>,
    val styles: List<String>,
    @JsonProperty("tracklist")
    val trackList: List<Track>,
    @JsonProperty("extraartists")
    val extraArtists: List<Artist>,
)

data class Artist(
    val name: String,
    val role: String
)

data class Track(
    val position: String,
    val title: String,
    @JsonProperty("extraartists")
    val extraArtists: List<Artist>,
    val duration: String
)
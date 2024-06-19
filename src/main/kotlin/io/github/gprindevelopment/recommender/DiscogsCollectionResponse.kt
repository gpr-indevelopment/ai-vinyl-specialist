package io.github.gprindevelopment.recommender

import com.fasterxml.jackson.annotation.JsonProperty

data class DiscogsCollectionResponse(
    val pagination: Pagination,
    val releases: List<Release>
)

data class Pagination(
    val page: Int,
    val pages: Int,
    @JsonProperty("per_page") val perPage: Int,
    val items: Int,
    val urls: Urls
)

data class Urls(
    val last: String,
    val next: String
)

data class Release(
    val id: Int,
    @JsonProperty("basic_information") val basicInformation: BasicInformation
)

data class BasicInformation(
    val id: Int,
    val title: String,
    val year: Int,
    val artists: List<Artist>,
    val labels: List<Label>,
    val genres: List<String>,
    val styles: List<String>
)

data class Artist(
    val id: Int,
    val name: String
)

data class Label(
    val name: String,
    val id: Int
)
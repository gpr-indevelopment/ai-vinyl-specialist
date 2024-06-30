package io.github.gprindevelopment.recommender.discogs

import com.fasterxml.jackson.annotation.JsonProperty

data class DiscogsCollectionResponse(
    val pagination: Pagination,
    @JsonProperty("releases")
    val releases: List<ReleaseResponse>
)

data class Pagination(
    val page: Int,
    val pages: Int,
    @JsonProperty("per_page") val perPage: Int,
    val items: Int
)

data class ReleaseResponse(
    val id: Int,
    @JsonProperty("basic_information") val basicInformation: BasicInformation
)

data class BasicInformation(
    val id: Int,
    val title: String,
    val year: Int,
    @JsonProperty("artists")
    val artists: List<ArtistResponse>,
    @JsonProperty("labels")
    val labels: List<LabelResponse>,
    val genres: List<String>,
    val styles: List<String>,
    val formats: List<FormatResponse>
)

data class ArtistResponse(
    val id: Int,
    val name: String
)

data class LabelResponse(
    val name: String,
    val id: Int
)

data class FormatResponse(
    val name: String,
    val qty: String,
    val descriptions: List<String>
) {
    fun isVinyl(): Boolean {
        return this.name.equals("vinyl", ignoreCase = true)
    }
}
package io.github.gprindevelopment.recommender.discogs

import org.springframework.stereotype.Service

@Service
class DiscogsService(
    val discogsClient: DiscogsClient
) {
    fun getFullCollection(user: DiscogsUser): List<SimpleVinylRecord> {
        val vinylRecords = mutableListOf<SimpleVinylRecord>()
        var currentPage = 0
        var response: DiscogsCollectionResponse
        do {
            currentPage++
            response = discogsClient.getCollection(user.username, currentPage)
            vinylRecords.addAll(response.toVinylRecords())
        } while (currentPage < response.pagination.pages)

        return vinylRecords
    }

    private fun DiscogsCollectionResponse.toVinylRecords(): List<SimpleVinylRecord> {
        return this.releases
            .filter { it.basicInformation.formats.any { format -> format.isVinyl() } }
            .map { SimpleVinylRecord(it.basicInformation.title, it.basicInformation.artists.first().name, it.id) }
    }
}
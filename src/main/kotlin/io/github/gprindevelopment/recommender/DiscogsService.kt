package io.github.gprindevelopment.recommender

import org.springframework.stereotype.Service

@Service
class DiscogsService(
    val discogsClient: DiscogsClient
) {
    fun getFullCollection(user: DiscogsUser): List<VinylRecord> {
        val vinylRecords = mutableListOf<VinylRecord>()
        var currentPage = 0
        var response: DiscogsCollectionResponse
        do {
            currentPage++
            response = discogsClient.getCollection(user.username, currentPage)
            vinylRecords.addAll(response.toVinylRecords())
        } while (currentPage < response.pagination.pages)

        return vinylRecords
    }

    private fun DiscogsCollectionResponse.toVinylRecords(): List<VinylRecord> {
        return this.releases
            .filter { it.basicInformation.formats.any { format -> format.isVinyl() } }
            .map { VinylRecord(it.basicInformation.title, it.basicInformation.artists.first().name) }
    }
}
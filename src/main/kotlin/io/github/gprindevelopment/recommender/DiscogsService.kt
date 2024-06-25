package io.github.gprindevelopment.recommender

import org.springframework.stereotype.Service

@Service
class DiscogsService(
    val discogsClient: DiscogsClient
) {
    fun getFullCollection(user: DiscogsUser): List<VinylRecord> {
        return discogsClient.getCollection(user.username).toVinylRecords()
    }

    private fun DiscogsCollectionResponse.toVinylRecords(): List<VinylRecord> {
        return this.releases
            .filter { it.basicInformation.formats.any { format -> format.isVinyl() } }
            .map { VinylRecord(it.basicInformation.title, it.basicInformation.artists.first().name) }
    }
}
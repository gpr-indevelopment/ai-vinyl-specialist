package io.github.gprindevelopment.recommender.discogs

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL

@Service
class DiscogsService(
    val discogsClient: DiscogsClient,
    val cache: DiscogsCache
) {
    private val logger = LoggerFactory.getLogger(DiscogsService::class.java)

    fun getFullCollection(user: DiscogsUser): List<SimpleVinylRecord> {
        val vinylRecords = mutableListOf<SimpleVinylRecord>()
        var currentPage = 0
        var response: DiscogsCollectionResponse
        do {
            currentPage++
            response = discogsClient.getCollection(user.username, currentPage)
            response.releases.forEach { cache.store(it) }
            vinylRecords.addAll(response.toVinylRecords())
        } while (currentPage < response.pagination.pages)

        return vinylRecords
    }

    fun enrichVinylRecord(vinylRecord: SimpleVinylRecord): EnrichedVinylRecord {
        val release = cache.getRelease(vinylRecord.releaseId)
        if (release == null) logger.warn("Unable to retrieve release with ID ${vinylRecord.releaseId} from Discogs cache. Enriched version will be missing data.")
        return EnrichedVinylRecord(vinylRecord.title, vinylRecord.artist, vinylRecord.releaseId, release?.basicInformation?.coverImage?.let { URL(it) })
    }

    private fun DiscogsCollectionResponse.toVinylRecords(): List<SimpleVinylRecord> {
        return this.releases
            .filter { it.basicInformation.formats.any { format -> format.isVinyl() } }
            .map { SimpleVinylRecord(it.basicInformation.title, it.basicInformation.artists.first().name, it.id) }
    }
}
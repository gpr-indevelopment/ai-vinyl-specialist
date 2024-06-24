package io.github.gprindevelopment.recommender

import dev.langchain4j.service.TokenStream
import org.springframework.stereotype.Service

data class DiscogsUser(val username: String)

@Service
class DiscogsVinylRecommenderService(
    val discogsClient: DiscogsClient,
    val assistant: VinylRecommenderAssistant
) {

    fun startRecommender(user: DiscogsUser): TokenStream {
        val collectionResponse = discogsClient.getCollection(user.username)
        val vinylRecords = collectionResponse.toVinylRecords()
        return assistant.chat("Hello!", vinylRecords.toCommaSeparatedList())
    }
}

private fun DiscogsCollectionResponse.toVinylRecords(): List<VinylRecord> {
    return this.releases
        .filter { it.basicInformation.formats.any { format -> format.isVinyl() } }
        .map { VinylRecord(it.basicInformation.title, it.basicInformation.artists.first().name) }
}

private fun List<VinylRecord>.toCommaSeparatedList(): String {
    return this.joinToString(", ") { "${it.artist}: ${it.title}" }
}
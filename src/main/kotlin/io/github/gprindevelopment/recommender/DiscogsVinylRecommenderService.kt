package io.github.gprindevelopment.recommender

import dev.langchain4j.service.TokenStream
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DiscogsVinylRecommenderService(
    val discogsClient: DiscogsClient,
    val assistant: VinylRecommenderAssistant
) {

    fun startRecommender(user: DiscogsUser): RecommenderSession {
        //TODO: What if nothing is found in the collection?
        val collectionResponse = discogsClient.getCollection(user.username)
        val vinylRecords = collectionResponse.toVinylRecords()
        return RecommenderSession(
            UUID.randomUUID(),
            user,
            vinylRecords
        )
    }

    fun chat(session: RecommenderSession, message: String): TokenStream {
        return assistant.chat(message, session.collection, session.memoryId)
    }
}

private fun DiscogsCollectionResponse.toVinylRecords(): List<VinylRecord> {
    return this.releases
        .filter { it.basicInformation.formats.any { format -> format.isVinyl() } }
        .map { VinylRecord(it.basicInformation.title, it.basicInformation.artists.first().name) }
}
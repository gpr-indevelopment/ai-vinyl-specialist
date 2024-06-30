package io.github.gprindevelopment.recommender.server

import dev.langchain4j.service.TokenStream
import io.github.gprindevelopment.recommender.assistant.ollama.VinylRecommenderAssistant
import io.github.gprindevelopment.recommender.discogs.DiscogsService
import io.github.gprindevelopment.recommender.discogs.DiscogsUser
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DiscogsVinylRecommenderService(
    val discogsService: DiscogsService,
    val assistant: VinylRecommenderAssistant
) {

    fun startRecommender(user: DiscogsUser): RecommenderSession {
        //TODO: What if nothing is found in the collection?
        val fullCollection = discogsService.getFullCollection(user)
        return RecommenderSession(
            UUID.randomUUID(),
            user,
            fullCollection
        )
    }

    fun chat(session: RecommenderSession, message: String): TokenStream {
        return assistant.chat(message, session.collection, session.memoryId)
    }
}
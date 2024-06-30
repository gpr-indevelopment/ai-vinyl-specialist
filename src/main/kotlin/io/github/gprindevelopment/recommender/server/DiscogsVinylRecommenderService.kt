package io.github.gprindevelopment.recommender.server

import dev.langchain4j.service.TokenStream
import io.github.gprindevelopment.recommender.assistant.openai.OpenAIVinylRecommenderAssistant
import io.github.gprindevelopment.recommender.discogs.DiscogsService
import io.github.gprindevelopment.recommender.discogs.DiscogsUser
import org.springframework.stereotype.Service
import java.util.*

@Service
class DiscogsVinylRecommenderService(
    val discogsService: DiscogsService,
    val assistant: OpenAIVinylRecommenderAssistant
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

    //TODO: Can we make the model selectable from the UI?
    fun chat(session: RecommenderSession, message: String): TokenStream {
        return assistant.chat(message, session.collection, session.memoryId)
    }
}
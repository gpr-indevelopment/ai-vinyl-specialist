package io.github.gprindevelopment.recommender.server

import dev.langchain4j.service.TokenStream
import io.github.gprindevelopment.recommender.assistant.ollama.OllamaVinylRecommenderAssistant
import io.github.gprindevelopment.recommender.discogs.DiscogsService
import io.github.gprindevelopment.recommender.discogs.DiscogsUser
import io.github.gprindevelopment.recommender.domain.VinylRecord
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockKExtension::class)
class DiscogsVinylRecommenderServiceTest {

    @InjectMockKs
    private lateinit var discogsVinylRecommenderService: DiscogsVinylRecommenderService

    @MockK
    private lateinit var discogsService: DiscogsService

    @MockK
    private lateinit var assistant: OllamaVinylRecommenderAssistant

    @Test
    fun `Should successfully start recommender session`() {
        val user = DiscogsUser("some-user")
        every { discogsService.getFullCollection(user) } returns listOf(
            VinylRecord("Abbey Road", "The Beatles"),
            VinylRecord("Let It Be", "The Beatles")
        )

        val session = discogsVinylRecommenderService.startRecommender(user)
        assertEquals(listOf(
            VinylRecord("Abbey Road", "The Beatles"),
            VinylRecord("Let It Be", "The Beatles")
        ), session.collection)
        assertEquals(user, session.user)
        assertNotNull(session.memoryId)
    }

    @Test
    fun `Should successfully use a Recommender Session to chat`() {
        val memoryId = UUID.randomUUID()
        val collection = listOf(VinylRecord("Abbey Road", "The Beatles"))
        val session = RecommenderSession(
            memoryId,
            DiscogsUser("some-user"),
            collection
        )
        val expectedStream = mockk<TokenStream>()

        every { assistant.chat("Hello!", collection, memoryId) } returns expectedStream

        val stream = discogsVinylRecommenderService.chat(session, "Hello!")
        assertEquals(expectedStream, stream)
    }
}
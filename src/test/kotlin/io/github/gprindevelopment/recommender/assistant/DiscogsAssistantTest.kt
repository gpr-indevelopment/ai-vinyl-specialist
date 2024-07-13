package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.model.output.TokenUsage
import dev.langchain4j.service.Result
import io.github.gprindevelopment.recommender.discogs.DiscogsService
import io.github.gprindevelopment.recommender.discogs.EnrichedVinylRecord
import io.github.gprindevelopment.recommender.discogs.SimpleVinylRecord
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.URL
import java.util.*

@ExtendWith(MockKExtension::class)
class DiscogsAssistantTest {

    @InjectMockKs
    private lateinit var discogsAssistant: DiscogsAssistant

    @MockK
    private lateinit var recommender: OpenAIVinylRecommenderAssistant

    @MockK
    private lateinit var discogsService: DiscogsService

    @Test
    fun `Should chat when no recommendations`() {
        val input = "Hello!"
        val memoryId = UUID.randomUUID()
        val expectedRecMessage = "Hello back!"
        val recommenderResult = Result.builder<RecommenderResponse>()
            .tokenUsage(TokenUsage(0, 0))
            .content(RecommenderResponse(expectedRecMessage, emptyList()))
            .build()
        val expectedEnriched = Result.builder<EnrichedRecommenderResponse>()
            .tokenUsage(TokenUsage(0, 0))
            .content(EnrichedRecommenderResponse(expectedRecMessage, emptyList()))
            .build()

        every { recommender.chatSync(input, memoryId) } returns recommenderResult

        val actual = discogsAssistant.chat(input, memoryId)
        assertEquals(actual.tokenUsage(), expectedEnriched.tokenUsage())
        assertEquals(actual.content(), expectedEnriched.content())
    }

    @Test
    fun `Should enrich and chat when there are recommendations`() {
        val input = "Hello!"
        val memoryId = UUID.randomUUID()
        val expectedRecMessage = "Hello back!"
        val recommendations = listOf(
            SimpleVinylRecord("Abbey Road", "The Beatles", 1)
        )
        val recommenderResult = Result.builder<RecommenderResponse>()
            .tokenUsage(TokenUsage(0, 0))
            .content(RecommenderResponse(expectedRecMessage, recommendations))
            .build()
        val expectedEnrichedRecommendations = listOf(
            EnrichedVinylRecord("Abbey Road", "The Beatles", 1, URL("http://localhost:8080/abbey-road"))
        )
        val expectedEnriched = Result.builder<EnrichedRecommenderResponse>()
            .tokenUsage(TokenUsage(0, 0))
            .content(EnrichedRecommenderResponse(expectedRecMessage, expectedEnrichedRecommendations))
            .build()

        every { recommender.chatSync(input, memoryId) } returns recommenderResult
        every { discogsService.enrichVinylRecord(recommendations[0]) } returns expectedEnrichedRecommendations[0]

        val actual = discogsAssistant.chat(input, memoryId)
        assertEquals(actual.tokenUsage(), expectedEnriched.tokenUsage())
        assertEquals(actual.content(), expectedEnriched.content())
    }
}
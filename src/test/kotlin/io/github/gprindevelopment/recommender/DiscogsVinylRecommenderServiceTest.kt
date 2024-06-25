package io.github.gprindevelopment.recommender

import dev.langchain4j.service.TokenStream
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
    private lateinit var discogsClient: DiscogsClient

    @MockK
    private lateinit var assistant: VinylRecommenderAssistant

    @Test
    fun `Should successfully start recommender session`() {
        val user = DiscogsUser("some-user")
        every { discogsClient.getCollection(user.username) } returns DiscogsResponseMother().discogsResponse()

        val session = discogsVinylRecommenderService.startRecommender(user)
        assertEquals(listOf(VinylRecord("Abbey Road", "The Beatles"),
            VinylRecord("Let It Be", "The Beatles")), session.collection)
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

class DiscogsResponseMother {

    fun discogsResponse(): DiscogsCollectionResponse {
        return DiscogsCollectionResponse(
            Pagination(1, 1, 1, 1, Urls("", "")),
            listOf(
                ReleaseResponse(
                    1,
                    BasicInformation(
                        1,
                        "Abbey Road",
                        1969,
                        listOf(ArtistResponse(1, "The Beatles")),
                        listOf(LabelResponse("Apple Records", 1)),
                        listOf("Rock"),
                        listOf("Pop Rock"),
                        listOf(FormatResponse("Vinyl", "1", listOf("LP")))
                    )
                ),
                ReleaseResponse(
                    2,
                    BasicInformation(
                        2,
                        "Let It Be",
                        1970,
                        listOf(ArtistResponse(1, "The Beatles")),
                        listOf(LabelResponse("Apple Records", 1)),
                        listOf("Rock"),
                        listOf("Pop Rock"),
                        listOf(FormatResponse("Vinyl", "1", listOf("LP")))
                    )
                ),
                ReleaseResponse(
                    3,
                    BasicInformation(
                        3,
                        "1962-1966",
                        1973,
                        listOf(ArtistResponse(1, "The Beatles")),
                        listOf(LabelResponse("Apple Records", 1)),
                        listOf("Rock"),
                        listOf("Pop Rock"),
                        listOf(FormatResponse("CD", "1", listOf("CD")))
                    )
                ),
            )
        )
    }
}
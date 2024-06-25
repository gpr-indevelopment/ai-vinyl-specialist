package io.github.gprindevelopment.recommender

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertContains
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class DiscogsServiceTest {

    @InjectMockKs
    private lateinit var discogsService: DiscogsService

    @MockK
    private lateinit var discogsClient: DiscogsClient

    @Test
    fun `Should get full collection with only vinyl records`() {
        val user = DiscogsUser("some-user")

        every { discogsClient.getCollection(user.username) } returns DiscogsResponseMother().discogsResponse()

        val vinylRecords = discogsService.getFullCollection(user)
        assertTrue(vinylRecords.size == 2)
        assertContains(vinylRecords, VinylRecord("Abbey Road", "The Beatles"))
        assertContains(vinylRecords, VinylRecord("Let It Be", "The Beatles"))
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
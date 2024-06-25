package io.github.gprindevelopment.recommender

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertContains
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class DiscogsServiceTest {

    @InjectMockKs
    private lateinit var discogsService: DiscogsService

    @MockK
    private lateinit var discogsClient: DiscogsClient

    @Test
    fun `Should get full collection with only vinyl records`() {
        val user = DiscogsUser("some-user")

        every { discogsClient.getCollection(user.username) } returns DiscogsResponseMother().discogsResponse(totalPages = 1)

        val vinylRecords = discogsService.getFullCollection(user)
        assertEquals(2, vinylRecords.size)
        assertContains(vinylRecords, VinylRecord("Abbey Road", "The Beatles"))
        assertContains(vinylRecords, VinylRecord("Let It Be", "The Beatles"))
    }

    @Test
    fun `Should get full collection from all available discogs pages`() {
        val user = DiscogsUser("some-user")

        every { discogsClient.getCollection(user.username, 1) } returns DiscogsResponseMother().discogsResponse(page = 1)
        every { discogsClient.getCollection(user.username, 2) } returns DiscogsResponseMother().discogsResponse(page = 2)
        every { discogsClient.getCollection(user.username, 3) } returns DiscogsResponseMother().discogsResponse(page = 3)

        val vinylRecords = discogsService.getFullCollection(user)
        assertEquals(6, vinylRecords.size)
    }

    @Test
    fun `Should return empty list when collection is empty`() {
        val user = DiscogsUser("some-user")

        every { discogsClient.getCollection(user.username) } returns DiscogsResponseMother().emptyDiscogsResponse()

        val vinylRecords = discogsService.getFullCollection(user)
        assertEquals(0, vinylRecords.size)
    }
}

class DiscogsResponseMother {

    fun emptyDiscogsResponse(): DiscogsCollectionResponse {
        return DiscogsCollectionResponse(
            Pagination(1, 0, 0, 0, Urls("", "")),
            emptyList()
        )
    }

    fun discogsResponse(page: Int = 1, totalPages: Int = 3): DiscogsCollectionResponse {
        return DiscogsCollectionResponse(
            Pagination(page, totalPages, 3, 3 * totalPages, Urls("", "")),
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
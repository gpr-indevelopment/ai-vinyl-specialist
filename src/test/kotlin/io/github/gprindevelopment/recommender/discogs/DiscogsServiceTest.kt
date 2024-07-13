package io.github.gprindevelopment.recommender.discogs

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.URL
import kotlin.test.assertContains
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class DiscogsServiceTest {

    @InjectMockKs
    private lateinit var discogsService: DiscogsService

    @MockK
    private lateinit var discogsClient: DiscogsClient

    @MockK
    private lateinit var discogsCache: DiscogsCache

    @Test
    fun `Should get full collection with only vinyl records`() {
        val user = DiscogsUser("some-user")

        every { discogsClient.getCollection(user.username) } returns DiscogsResponseMother().discogsResponse(totalPages = 1)
        every { discogsCache.store(any()) } just runs

        val vinylRecords = discogsService.getFullCollection(user)
        assertEquals(2, vinylRecords.size)
        assertContains(vinylRecords, SimpleVinylRecord("Abbey Road", "The Beatles", 1))
        assertContains(vinylRecords, SimpleVinylRecord("Let It Be", "The Beatles", 2))
        verify(atLeast = 1) { discogsCache.store(any()) }
    }

    @Test
    fun `Should get full collection from all available discogs pages`() {
        val user = DiscogsUser("some-user")

        every { discogsClient.getCollection(user.username, 1) } returns DiscogsResponseMother().discogsResponse(page = 1)
        every { discogsClient.getCollection(user.username, 2) } returns DiscogsResponseMother().discogsResponse(page = 2)
        every { discogsClient.getCollection(user.username, 3) } returns DiscogsResponseMother().discogsResponse(page = 3)
        every { discogsCache.store(any()) } just runs

        val vinylRecords = discogsService.getFullCollection(user)
        assertEquals(6, vinylRecords.size)
        verify(atLeast = 3) { discogsCache.store(any()) }
    }

    @Test
    fun `Should return empty list when collection is empty`() {
        val user = DiscogsUser("some-user")

        every { discogsClient.getCollection(user.username) } returns DiscogsResponseMother().emptyDiscogsResponse()

        val vinylRecords = discogsService.getFullCollection(user)
        assertEquals(0, vinylRecords.size)
    }

    @Test
    fun `Should enrich vinyl record using in-memory cache`() {
        val releaseId = 1
        val record = SimpleVinylRecord("Abbey Road", "The Beatles", releaseId)
        val expectedEnrichedRecord = EnrichedVinylRecord("Abbey Road", "The Beatles", releaseId, URL(DiscogsResponseMother.DEFAULT_COVER_IMAGE))

        every { discogsCache.getRelease(releaseId) } returns DiscogsResponseMother().discogsRelease()
        val actualEnrichedRecord = discogsService.enrichVinylRecord(record)
        assertEquals(expectedEnrichedRecord, actualEnrichedRecord)
    }

    @Test
    fun `Should enrich vinyl record while missing cover image data when release cached`() {
        val releaseId = 1
        val record = SimpleVinylRecord("Abbey Road", "The Beatles", releaseId)
        val expectedEnrichedRecord = EnrichedVinylRecord("Abbey Road", "The Beatles", releaseId)

        every { discogsCache.getRelease(releaseId) } returns null
        val actualEnrichedRecord = discogsService.enrichVinylRecord(record)
        assertEquals(expectedEnrichedRecord, actualEnrichedRecord)
    }
}

class DiscogsResponseMother {

    companion object {
        const val DEFAULT_COVER_IMAGE = "http://localhost/some-cover-image-url"
    }

    fun emptyDiscogsResponse(): DiscogsCollectionResponse {
        return DiscogsCollectionResponse(
            Pagination(1, 0, 0, 0),
            emptyList()
        )
    }

    fun discogsRelease(): ReleaseResponse {
        return ReleaseResponse(
            1,
            BasicInformation(
                1,
                "Abbey Road",
                1969,
                listOf(ArtistResponse(1, "The Beatles")),
                listOf(LabelResponse("Apple Records", 1)),
                listOf("Rock"),
                listOf("Pop Rock"),
                listOf(FormatResponse("Vinyl", "1", listOf("LP"))),
                DEFAULT_COVER_IMAGE
            )
        )
    }

    fun discogsResponse(page: Int = 1,
                        totalPages: Int = 3,
                        coverImage: String = DEFAULT_COVER_IMAGE): DiscogsCollectionResponse {
        return DiscogsCollectionResponse(
            Pagination(page, totalPages, 3, 3 * totalPages),
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
                        listOf(FormatResponse("Vinyl", "1", listOf("LP"))),
                        coverImage
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
                        listOf(FormatResponse("Vinyl", "1", listOf("LP"))),
                        coverImage
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
                        listOf(FormatResponse("CD", "1", listOf("CD"))),
                        coverImage
                    )
                ),
            )
        )
    }
}
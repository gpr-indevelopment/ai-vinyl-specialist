package io.github.gprindevelopment.recommender.discogs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNull

class DiscogsCacheTest {

    @Test
    fun `Should store a release in the cache`() {
        val release = ReleaseResponse(
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
        )
        val cache = DiscogsCache()
        cache.store(release)
        assertEquals(release, cache.getRelease(2))
    }

    @Test
    fun `Should return null when cache miss`() {
        val cache = DiscogsCache()
        assertNull(cache.getRelease(2))
    }
}
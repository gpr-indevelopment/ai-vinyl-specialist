package io.github.gprindevelopment.recommender.discogs

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
class DiscogsClientTest {

    @Autowired
    private lateinit var discogsClient: DiscogsClient

    @Test
    fun `Should get collection from Discogs API`() {
        val collection = discogsClient.getCollection("brunacitrini")
        val expectedAbbeyRoadCoverImage = "https://i.discogs.com/M2yc5OJZPdVoDm2_UlRRXuDlDguamLLSdnbziNmZoQI/rs:fit/g:sm/q:90/h:600/w:600/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTI2MDc0/MjQtMTYzMDYwMTA4/Ny0xMTk5LmpwZWc.jpeg"
        val abbeyRoad = collection.releases.find { it.basicInformation.title == "Abbey Road" }
        assertNotNull(abbeyRoad)
        assertTrue(abbeyRoad.basicInformation.artists.any { it.name == "The Beatles" })
        assertEquals(expectedAbbeyRoadCoverImage, abbeyRoad.basicInformation.coverImage)
    }
}
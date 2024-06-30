package io.github.gprindevelopment.recommender.discogs

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertTrue

@SpringBootTest
class DiscogsClientTest {

    @Autowired
    private lateinit var discogsClient: DiscogsClient

    @Test
    fun `Should get collection from Discogs API`() {
        val collection = discogsClient.getCollection("brunacitrini")
        assertTrue(collection.releases.any { it.basicInformation.title == "Abbey Road"
                && it.basicInformation.artists.any { it.name == "The Beatles" } })
    }
}
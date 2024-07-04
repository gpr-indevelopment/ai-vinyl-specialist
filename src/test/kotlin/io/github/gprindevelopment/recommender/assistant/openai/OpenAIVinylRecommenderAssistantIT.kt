package io.github.gprindevelopment.recommender.assistant.openai

import com.ninjasquad.springmockk.MockkBean
import io.github.gprindevelopment.recommender.assistant.StreamAssertions.Companion.assertStreamContainsOneOf
import io.github.gprindevelopment.recommender.assistant.StreamAssertions.Companion.assertStreamDoesNotContainOneOf
import io.github.gprindevelopment.recommender.discogs.DiscogsService
import io.github.gprindevelopment.recommender.discogs.DiscogsUser
import io.github.gprindevelopment.recommender.domain.VinylRecord
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.*

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class OpenAIVinylRecommenderAssistantIT {

    @Autowired
    private lateinit var assistant: OpenAIVinylRecommenderAssistant

    @MockkBean
    private lateinit var discogsService: DiscogsService

    private val vinylCollection = listOf(
        VinylRecord("Zenyatta Mondatta", "The Police"),
        VinylRecord("Paris", "Supertramp"),
        VinylRecord("Bring On The Night", "Sting"),
        VinylRecord("The Autobiography Of Supertramp", "Supertramp"),
        VinylRecord("Carpenters", "Carpenters"),
        VinylRecord("An Evening With Silk Sonic", "Silk Sonic"),
        VinylRecord("Abbey Road", "The Beatles"),
        VinylRecord("1962-1966", "The Beatles"),
        VinylRecord("1967-1970", "The Beatles"),
        VinylRecord("Let It Be", "The Beatles")
    )

    @Test
    fun `Should recommend vinyl records from collection retrieved via discogs tool`() {
        val message = """
            Hello! Can you recommend me a Beatles record?
            This is a test. You must answer with the recommended title only, nothing else.
            My Discogs username is test.
        """.trimIndent()
        val beatlesTitles = vinylCollection.filter { it.artist.contains("Beatles") }.map { it.title }

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val response = assistant.chat(message)
        assertStreamContainsOneOf(response, beatlesTitles, Duration.ofSeconds(30))
    }

    @Test
    fun `Should not recommend vinyl records if Discogs username is not provided`() {
        val message = """
            Hello! Can you recommend me a Beatles record?
        """.trimIndent()
        val titles = vinylCollection.map { it.title }

        val response = assistant.chat(message)
        assertStreamDoesNotContainOneOf(response, titles, Duration.ofSeconds(30))

        verify(inverse = true) { discogsService.getFullCollection(any()) }
    }

    @Test
    fun `Should not recommend vinyl records if Discogs collection is empty`() {
        val message = """
            Hello! Can you recommend me a Beatles record?
            My Discogs username is test.
        """.trimIndent()
        val titles = vinylCollection.map { it.title }

        every { discogsService.getFullCollection(DiscogsUser("testa")) } returns emptyList()
        val response = assistant.chat(message)
        assertStreamDoesNotContainOneOf(response, titles, Duration.ofSeconds(30))
    }

    @Test
    fun `When asking two recommendations, should not fetch Discogs twice`() {
        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val memory = UUID.randomUUID()
        val beatlesTitles = vinylCollection.filter { it.artist.contains("Beatles") }.map { it.title }
        val supertrampTitles = vinylCollection.filter { it.artist.contains("Supertramp") }.map { it.title }
        val message1 = """
            Hello! Can you recommend me a Beatles record?
            This is a test. You must answer with the recommended title only, nothing else.
            My Discogs username is test.
        """.trimIndent()
        val response1 = assistant.chat(message1, memory)
        assertStreamContainsOneOf(response1, beatlesTitles, Duration.ofSeconds(30))

        val message2 = """
            Thanks! Can you now recommend me a Supertramp record?
        """.trimIndent()
        val response2 = assistant.chat(message2, memory)
        assertStreamContainsOneOf(response2, supertrampTitles, Duration.ofSeconds(30))

        verify(atMost = 1) { discogsService.getFullCollection(DiscogsUser("test")) }
    }
}
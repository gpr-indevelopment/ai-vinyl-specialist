package io.github.gprindevelopment.recommender.assistant

import com.ninjasquad.springmockk.MockkBean
import io.github.gprindevelopment.recommender.assistant.reviewer.TestReviewAssistant
import io.github.gprindevelopment.recommender.discogs.DiscogsService
import io.github.gprindevelopment.recommender.discogs.DiscogsUser
import io.github.gprindevelopment.recommender.domain.VinylRecord
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.net.URL
import java.util.*
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
class OpenAIVinylRecommenderAssistantIT {

    @Autowired
    private lateinit var assistant: OpenAIVinylRecommenderAssistant

    @MockkBean
    private lateinit var discogsService: DiscogsService

    @Autowired
    private lateinit var testReviewAssistant: TestReviewAssistant

    private val vinylCollection = listOf(
        VinylRecord("Zenyatta Mondatta", "The Police", URL("https://localhost/zenyatta-mondatta.jpg")),
        VinylRecord("Paris", "Supertramp", URL("https://localhost/paris.jpg")),
        VinylRecord("Bring On The Night", "Sting", URL("https://localhost/bring-on-the-night.jpg")),
        VinylRecord("The Autobiography Of Supertramp", "Supertramp", URL("https://localhost/the-autobiography-of-supertramp.jpg")),
        VinylRecord("Carpenters", "Carpenters", URL("https://localhost/carpenters.jpg")),
        VinylRecord("An Evening With Silk Sonic", "Silk Sonic", URL("https://localhost/an-evening-with-silk-sonic.jpg")),
        VinylRecord("Abbey Road", "The Beatles", URL("https://localhost/abbey-road.jpg")),
        VinylRecord("1962-1966", "The Beatles", URL("https://localhost/1962-1966.jpg")),
        VinylRecord("1967-1970", "The Beatles", URL("https://localhost/1967-1970.jpg")),
        VinylRecord("Let It Be", "The Beatles", URL("https://localhost/let-it-be.jpg"))
    )

    @Test
    fun `Should recommend vinyl records from collection retrieved via discogs tool`() {
        val message = """
            Hello! Can you recommend me a Beatles record? My Discogs username is test.
        """.trimIndent()

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val response = assistant.chatSync(message)
        assertTrue(testReviewAssistant.review("The chatbot recommended a single record from the Beatles, and no other records", response.message).answer)
    }

    @Test
    fun `Should not recommend vinyl records if Discogs username is not provided`() {
        val message = """
            Hello! Can you recommend me a Beatles record?
        """.trimIndent()

        val response = assistant.chatSync(message)
        assertTrue(testReviewAssistant.review("The chatbot asked for a Discogs username instead of providing a recommendation", response.message).answer)

        verify(inverse = true) { discogsService.getFullCollection(any()) }
    }

    @Test
    fun `Should not recommend vinyl records if Discogs collection is empty`() {
        val message = """
            Hello! Can you recommend me a Beatles record? My Discogs username is test.
        """.trimIndent()

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns emptyList()
        val response = assistant.chatSync(message)
        assertTrue(testReviewAssistant.review("The chatbot did not make any recommendations", response.message).answer)
    }

    @Test
    fun `When asking two recommendations, should not fetch Discogs twice`() {
        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val memory = UUID.randomUUID()
        val message1 = """
            Hello! Can you recommend me a Beatles record? My Discogs username is test.
        """.trimIndent()
        assistant.chatSync(message1, memory)

        val message2 = """
            Thanks! Can you now recommend me a Supertramp record?
        """.trimIndent()
        assistant.chatSync(message2, memory)

        verify(atMost = 1) { discogsService.getFullCollection(DiscogsUser("test")) }
    }

    @Test
    fun `Should only recommend records based on what was asked for`() {
        val message = """
            Hello! Can you recommend me 3 Beatles records? My Discogs username is test.
        """.trimIndent()

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val response = assistant.chatSync(message)
        assertTrue(testReviewAssistant.review("The chatbot recommended 3 records from The Beatles", response.message).answer)
    }

    @Test
    fun `Should not recommend records outside of the collection`() {
        val message = """
            Hello! Can you recommend me a Pink Floyd record? My Discogs username is test.
        """.trimIndent()

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val response = assistant.chatSync(message)
        assertTrue(testReviewAssistant.review("The chatbot did not recommend a Pink Floyd record", response.message).answer)
    }

    @Test
    fun `Recommendations should be mentioned in message and recommendations field`() {
        val message = """
            Hello! Can you recommend me a Sting record? My Discogs username is test.
        """.trimIndent()

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val response = assistant.chatSync(message)
        assertContains(response.message, "Sting")
        assertContains(response.message, "Bring On The Night")
        assertContains(response.recommendations, VinylRecord("Bring On The Night", "Sting", URL("https://localhost/bring-on-the-night.jpg")))
    }

    @Test
    fun `Cover image should be added to the recommendation object, but not the message`() {
        val message = """
            Hello! Can you recommend me a Sting record? My Discogs username is test.
        """.trimIndent()

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val response = assistant.chatSync(message)
        assertFalse(response.message.contains("https://localhost/bring-on-the-night.jpg"))
        assertContains(response.recommendations, VinylRecord("Bring On The Night", "Sting", URL("https://localhost/bring-on-the-night.jpg")))
    }
}
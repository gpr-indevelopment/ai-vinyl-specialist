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
import java.util.*
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
            My Discogs username is test.
        """.trimIndent()

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val response = assistant.chatSync(message)
        assertTrue(testReviewAssistant.review("Was there a single recommended record from the Beatles, and no other records?", response).answer)
    }

    @Test
    fun `Should not recommend vinyl records if Discogs username is not provided`() {
        val message = """
            Hello! Can you recommend me a Beatles record?
        """.trimIndent()

        val response = assistant.chatSync(message)
        assertTrue(testReviewAssistant.review("Did the chatbot ask for a Discogs username instead of providing a recommendation?", response).answer)

        verify(inverse = true) { discogsService.getFullCollection(any()) }
    }

    @Test
    fun `Should not recommend vinyl records if Discogs collection is empty`() {
        val message = """
            Hello! Can you recommend me a Beatles record?
            My Discogs username is test.
        """.trimIndent()

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns emptyList()
        val response = assistant.chatSync(message)
        assertFalse(testReviewAssistant.review("Did the chatbot provide any record recommendation?", response).answer)
    }

    @Test
    fun `When asking two recommendations, should not fetch Discogs twice`() {
        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val memory = UUID.randomUUID()
        val message1 = """
            Hello! Can you recommend me a Beatles record?
            This is a test. You must answer with the recommended title only, nothing else.
            My Discogs username is test.
        """.trimIndent()
        val response1 = assistant.chatSync(message1, memory)
        assertTrue(testReviewAssistant.review("Did the chatbot provide any record recommendation?", response1).answer)

        val message2 = """
            Thanks! Can you now recommend me a Supertramp record?
        """.trimIndent()
        val response2 = assistant.chatSync(message2, memory)
        assertTrue(testReviewAssistant.review("Did the chatbot provide any record recommendation?", response2).answer)

        verify(atMost = 1) { discogsService.getFullCollection(DiscogsUser("test")) }
    }

    @Test
    fun `Should only recommend records based on what was asked for`() {
        val message = """
            Hello! Can you recommend me 3 Beatles records?
            My Discogs username is test.
        """.trimIndent()

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val response = assistant.chatSync(message)
        assertTrue(testReviewAssistant.review("Were all the recommended records from The Beatles?", response).answer)
    }

    @Test
    fun `Should not recommend records outside of the collection`() {
        val message = """
            Hello! Can you recommend me a Pink Floyd record?
            My Discogs username is test.
        """.trimIndent()

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val response = assistant.chatSync(message)
        assertFalse(testReviewAssistant.review("Did the chatbot recommend any record?", response).answer)
    }
}
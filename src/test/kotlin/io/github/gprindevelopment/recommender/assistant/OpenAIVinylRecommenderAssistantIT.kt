package io.github.gprindevelopment.recommender.assistant

import com.ninjasquad.springmockk.MockkBean
import io.github.gprindevelopment.recommender.assistant.reviewer.TestReviewAssistant
import io.github.gprindevelopment.recommender.discogs.DiscogsService
import io.github.gprindevelopment.recommender.discogs.DiscogsUser
import io.github.gprindevelopment.recommender.discogs.SimpleVinylRecord
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
@Disabled("For cost savings, disabled until AI is changed")
class OpenAIVinylRecommenderAssistantIT {

    @Autowired
    private lateinit var assistant: OpenAIVinylRecommenderAssistant

    @MockkBean
    private lateinit var discogsService: DiscogsService

    @Autowired
    private lateinit var testReviewAssistant: TestReviewAssistant

    private val vinylCollection = listOf(
        SimpleVinylRecord("Zenyatta Mondatta", "The Police", 1),
        SimpleVinylRecord("Paris", "Supertramp", 2),
        SimpleVinylRecord("Bring On The Night", "Sting", 3),
        SimpleVinylRecord("The Autobiography Of Supertramp", "Supertramp", 4),
        SimpleVinylRecord("Carpenters", "Carpenters", 5),
        SimpleVinylRecord("An Evening With Silk Sonic", "Silk Sonic", 6),
        SimpleVinylRecord("Abbey Road", "The Beatles", 7),
        SimpleVinylRecord("1962-1966", "The Beatles", 8),
        SimpleVinylRecord("1967-1970", "The Beatles", 9),
        SimpleVinylRecord("Let It Be", "The Beatles", 10)
    )

    @Test
    fun `Should recommend vinyl records from collection retrieved via discogs tool`() {
        val message = """
            Hello! Can you recommend me a Beatles record? My Discogs username is test.
        """.trimIndent()

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val response = assistant.chatSync(message)
        assertTrue(testReviewAssistant.review("The chatbot recommended records from the Beatles, and not from other artists", response.message).answer)
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
        assertContains(response.recommendations, SimpleVinylRecord("Bring On The Night", "Sting", vinylCollection.first { it.title == "Bring On The Night" }.releaseId))
    }

    @Test
    fun `Cover image should be added to the recommendation object, but not the message`() {
        val message = """
            Hello! Can you recommend me a Sting record? My Discogs username is test.
        """.trimIndent()

        every { discogsService.getFullCollection(DiscogsUser("test")) } returns vinylCollection
        val response = assistant.chatSync(message)
        assertFalse(response.message.contains("https://localhost/bring-on-the-night.jpg"))
        assertContains(response.recommendations, SimpleVinylRecord("Bring On The Night", "Sting", vinylCollection.first { it.title == "Bring On The Night" }.releaseId))
    }
}
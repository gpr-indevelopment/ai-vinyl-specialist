package io.github.gprindevelopment.recommender

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

class VinylRecommenderAssistantIT: AssistantIT() {

    @Autowired
    lateinit var assistant: VinylRecommenderAssistant

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private data class Record(val artist: String, val title: String)

    private val collection = listOf(
        Record("The Police", "Zenyatta Mondatta"),
        Record("Supertramp", "Paris"),
        Record("Sting", "Bring On The Night"),
        Record("Supertramp", "The Autobiography Of Supertramp"),
        Record("Carpenters", "Carpenters"),
        Record("Silk Sonic", "An Evening With Silk Sonic"),
        Record("The Beatles", "Abbey Road"),
        Record("The Beatles", "1962-1966"),
        Record("The Beatles", "1967-1970"),
        Record("The Beatles", "Let It Be")
    )

    @Test
    fun `Should recommend vinyl records from collection`() {
        val message = """
            Hello! I am a customer looking for some recommendations.
            I am feeling a bit down and I want to feel better.
            I am looking for some records that have a chill vibe.
            This is a test. You must answer with the recommended title only, nothing else.
        """.trimIndent()
        val artistAndTitles = collection.map { "${it.artist}: ${it.title}" }
        val titles = collection.map { it.title }
        val response = assistant.chat(message, artistAndTitles.joinToString(", "))
        assertStreamContainsOneOf(response, titles, Duration.ofMinutes(5))
    }
}
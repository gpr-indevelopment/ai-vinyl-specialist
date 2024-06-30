package io.github.gprindevelopment.recommender.assistant.ollama

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.gprindevelopment.recommender.assistant.AssistantIT
import io.github.gprindevelopment.recommender.domain.VinylRecord
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

class VinylRecommenderAssistantIT: AssistantIT() {

    @Autowired
    private lateinit var assistant: VinylRecommenderAssistant

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private data class Record(val artist: String, val title: String)

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
    fun `Should recommend vinyl records from collection`() {
        val message = """
            Hello! I am a customer looking for some recommendations.
            I am feeling a bit down and I want to feel better.
            I am looking for some records that have a chill vibe.
            This is a test. You must answer with the recommended title only, nothing else.
        """.trimIndent()
        val titles = vinylCollection.map { it.title }
        val response = assistant.chat(message, vinylCollection)
        assertStreamContainsOneOf(response, titles, Duration.ofMinutes(5))
    }
}
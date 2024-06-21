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

    //TODO: Can we make it into a single object?
    val collection = listOf(
        "The Police: Zenyatta Mondatta",
        "Supertramp: Paris",
        "Sting: Bring On The Night",
        "Supertramp: The Autobiography Of Supertramp",
        "Carpenters: Carpenters",
        "Silk Sonic: An Evening With Silk Sonic",
        "The Beatles: Abbey Road",
        "The Beatles: 1962-1966",
        "The Beatles: 1967-1970",
        "The Beatles: Let It Be"
    )

    val titles = listOf(
        "Zenyatta Mondatta",
        "Paris",
        "Bring On The Night",
        "The Autobiography Of Supertramp",
        "Carpenters",
        "An Evening With Silk Sonic",
        "Abbey Road",
        "1962-1966",
        "1967-1970",
        "Let It Be"
    )

    @Test
    fun `Should recommend vinyl records from collection`() {
        val message = """
            Hello! I am a customer looking for some recommendations.
            I am feeling a bit down and I want to feel better.
            I am looking for some records that have a chill vibe.
            This is a test. You must answer with the recommended title only, nothing else.
        """.trimIndent()
        val response = assistant.chat(message, collection.joinToString(", "))
        assertStreamContainsOneOf(response, titles, Duration.ofMinutes(5))
    }
}
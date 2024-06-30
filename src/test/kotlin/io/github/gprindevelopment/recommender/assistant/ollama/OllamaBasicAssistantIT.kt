package io.github.gprindevelopment.recommender.assistant.ollama

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals

class OllamaBasicAssistantIT: OllamaAssistantTester() {

    @Autowired
    private lateinit var assistant: OllamaBasicAssistant

    @Test
    fun `Should stream chat with recommender`() {
        val input = "Hello! This is a test. Respond with the X character only, and nothing else."
        val outputStream = assistant.chat(input)

        assertStream(outputStream, "X")
    }

    @Test
    fun `Should chat with recommender`() {
        val input = "Hello! This is a test. Respond with the X character only, and nothing else."
        val response = assistant.chatSync(input)

        assertEquals("X", response)
    }

    @Test
    fun `Two users should stream chat with recommender maintaining memory`() {
        val maryMemoryId = UUID.randomUUID()
        val johnMemoryId = UUID.randomUUID()
        val maryPrompt = """
               Hello! I am Mary, and this is a test. 
               You must respond this message with a single X character only, and nothing else.
               Then, I will send you a new message with the Y character,
               and you must respond with the Z character, and nothing else.
            """
        val johnPrompt = """
               Hello! I am John, and this is a test. 
               You must respond this message with a single A character only, and nothing else.
               Then, I will send you a new message with the B character,
               and you must respond with the C character, and nothing else.
            """
        assertStream(assistant.chat(maryPrompt, maryMemoryId), "X")
        assertStream(assistant.chat(johnPrompt, johnMemoryId), "A")
        assertStream(assistant.chat("Y", maryMemoryId), "Z")
        assertStream(assistant.chat("B", johnMemoryId), "C")
    }
}
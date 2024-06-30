package io.github.gprindevelopment.recommender.assistant.openai

import dev.langchain4j.service.TokenStream
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.util.*
import java.util.function.Consumer
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
class OpenAIBasicAssistantIT {

    @Autowired
    private lateinit var assistant: OpenAIBasicAssistant

    @Test
    fun `Should stream chat with recommender`() {
        val input = "Hello! This is a test. Respond with the X character only, and nothing else."
        val outputStream = assistant.chat(input)

        assertStream(outputStream, "X")
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

    fun assertStream(stream: TokenStream, expected: String, timeout: Duration = Duration.ofMinutes(2)) {
        assertStreamInternal(stream, { response ->
            assertEquals(expected, response)
        }, timeout)
    }

    private fun assertStreamInternal(stream: TokenStream, responseConsumer: Consumer<String>, timeout: Duration = Duration.ofMinutes(2)) {
        var response: String? = null
        stream
            .onNext { }
            .onComplete { modelResponse -> response = modelResponse.content().text() }
            .ignoreErrors()
            .start()
        Awaitility.with()
            .pollInterval(Duration.ofSeconds(5))
            .and()
            .atMost(timeout)
            .await()
            .untilAsserted {
                assertNotNull(response)
                responseConsumer.accept(response!!)
            }
    }
}
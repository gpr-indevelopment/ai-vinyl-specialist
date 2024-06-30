package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.service.TokenStream
import org.awaitility.Awaitility
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.function.Consumer
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class AssistantTester {

    fun assertStream(stream: TokenStream, expected: String, timeout: Duration = Duration.ofMinutes(2)) {
        assertStreamInternal(stream, { response ->
            assertEquals(expected, response)
        }, timeout)
    }

    fun assertStreamContainsOneOf(stream: TokenStream, expected: List<String>, timeout: Duration = Duration.ofMinutes(2)) {
        assertStreamInternal(stream, { response ->
            assertTrue(expected.any { response.contains(it) })
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
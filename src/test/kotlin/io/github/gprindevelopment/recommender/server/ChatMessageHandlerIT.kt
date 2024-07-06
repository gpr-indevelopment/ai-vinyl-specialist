package io.github.gprindevelopment.recommender.server

import com.ninjasquad.springmockk.MockkBean
import dev.langchain4j.model.output.TokenUsage
import dev.langchain4j.service.Result
import io.github.gprindevelopment.recommender.assistant.OpenAIVinylRecommenderAssistant
import io.github.gprindevelopment.recommender.assistant.RecommenderResponse
import io.mockk.every
import org.awaitility.Awaitility.with
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.Duration
import kotlin.test.assertContains
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatMessageHandlerIT {

    @LocalServerPort
    private var port: Int = 0

    @MockkBean
    private lateinit var assistant: OpenAIVinylRecommenderAssistant

    @Test
    fun `Should successfully communicate through websockets`() {
        val expectedResponse = "Welcome to the recommender!"
        val handler = TestWsHandler()
        every { assistant.chatSync(any(), any()) } returns Result.builder<RecommenderResponse>()
            .tokenUsage(TokenUsage(0, 0))
            .content(RecommenderResponse(expectedResponse, emptyList()))
            .build()

        StandardWebSocketClient().execute(handler, "ws://localhost:$port/chat")
            .join()
        with()
            .atMost(Duration.ofSeconds(10))
            .await()
            .untilAsserted {
                assertEquals(2, handler.messagesReceived.size)
                assertContains(handler.messagesReceived, expectedResponse)
            }
    }

    private class TestWsHandler: TextWebSocketHandler() {

        val messagesReceived = mutableListOf<String>()

        override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
            messagesReceived.add(message.payload)
        }
    }
}
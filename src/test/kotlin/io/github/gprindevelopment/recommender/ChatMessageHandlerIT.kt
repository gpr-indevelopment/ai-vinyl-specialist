package io.github.gprindevelopment.recommender

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
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
    private lateinit var discogsVinylRecommenderService: DiscogsVinylRecommenderService

    @Test
    fun `Should successfully communicate through websockets`() {
        val expectedResponse = "Welcome to the recommender!"
        val handler = TestWsHandler()
        every { discogsVinylRecommenderService.startRecommender(any()) } returns mockk()
        every { discogsVinylRecommenderService.chat(any(), any()) } returns TestTokenStream(expectedResponse)

        StandardWebSocketClient().execute(handler, "ws://localhost:$port/chat?discogsUser=gabriel")
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
package io.github.gprindevelopment.recommender

import org.awaitility.Awaitility.with
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.Duration
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatMessageHandlerIT {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var chatMessageHandler: ChatMessageHandler;

    @Test
    fun Should_successfully_communicate_through_websockets() {
        val messages = mutableListOf<String>()
        val session = StandardWebSocketClient().execute(TestWsHandler(messages), "ws://localhost:$port/chat")
            .join()
        session.sendMessage(TextMessage("Hello"))
        with()
            .atMost(Duration.ofSeconds(10))
            .await()
            .untilAsserted {
                assertEquals(1, messages.size)
            }
    }

    private class TestWsHandler(val messages: MutableList<String>): TextWebSocketHandler() {
        override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
            messages.add(message.payload)
        }
    }
}
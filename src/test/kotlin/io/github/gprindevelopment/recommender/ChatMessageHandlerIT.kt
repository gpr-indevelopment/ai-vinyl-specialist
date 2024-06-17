package io.github.gprindevelopment.recommender

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import kotlin.test.assertNotNull


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatMessageHandlerIT {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var chatMessageHandler: ChatMessageHandler;

    @Test
    fun Should_successfully_communicate_through_websockets() {
        val session = StandardWebSocketClient().execute(TestWsHandler(), "ws://localhost:$port/chat")
            .join()
        session.sendMessage(TextMessage("Hello"))
    }

    private class TestWsHandler: TextWebSocketHandler() {
        override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
            assertNotNull(message.payload)
        }
    }
}
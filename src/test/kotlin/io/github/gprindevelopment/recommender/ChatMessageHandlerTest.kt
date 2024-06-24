package io.github.gprindevelopment.recommender

import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.web.socket.WebSocketSession
import java.net.URI
import kotlin.test.assertEquals

class ChatMessageHandlerTest {

    val chatMessageHandler: ChatMessageHandler = ChatMessageHandler(mockk())

    @ParameterizedTest
    @ValueSource(strings = ["ws://localhost:8080/chat", "ws://localhost:8080/chat/1234", "ws://localhost:8080/chat/1234?sessionid=1234"])
    fun `Should not start websocket without a session ID`() {
        val session = mockk<WebSocketSession>()

        every { session.uri } returns URI.create("ws://localhost:8080/chat")
        assertThrows<IllegalArgumentException> { chatMessageHandler.afterConnectionEstablished(session) }
    }

    @Test
    fun `Should not start websocket without a URI`() {
        val session = mockk<WebSocketSession>()

        every { session.uri } returns null
        assertThrows<IllegalArgumentException> { chatMessageHandler.afterConnectionEstablished(session) }
    }

    @Test
    fun `Should successfully start a websocket session`() {
        val session = mockk<WebSocketSession>()

        every { session.uri } returns URI.create("ws://localhost:8080/chat?sessionId=1234")
        every { session.attributes } returns mutableMapOf()
        every { session.sendMessage(any()) } just runs
        chatMessageHandler.afterConnectionEstablished(session)
        assertEquals("1234", session.attributes["sessionId"])
    }
}
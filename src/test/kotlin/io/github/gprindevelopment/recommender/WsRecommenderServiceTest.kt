package io.github.gprindevelopment.recommender

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.net.URI
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class WsRecommenderServiceTest {

    //TODO: Change all lateinit vars in test to private?
    @InjectMockKs
    private lateinit var wsRecommenderService: WsRecommenderService

    @MockK
    private lateinit var discogsRecommenderService: DiscogsVinylRecommenderService

    @Test
    fun `Should not start websocket without a URI`() {
        val session = mockk<WebSocketSession>()

        every { session.uri } returns null
        assertThrows<IllegalArgumentException> { wsRecommenderService.setupSession(session) }
    }

    @Test
    fun `Should not start websocket without a discogs user`() {
        val session = mockk<WebSocketSession>()

        every { session.uri } returns URI.create("ws://localhost:8080/chat?sessionId=1234")
        every { session.attributes } returns mutableMapOf()

        assertThrows<IllegalArgumentException> { wsRecommenderService.setupSession(session) }
    }

    @Test
    fun `Should chat through websockets`() {
        val inputString = "I want record recommendations!"
        val wsSession = mockk<WebSocketSession>()
        val expectedRecommenderSession = RecommenderSession(UUID.randomUUID(), DiscogsUser("gabriel"), listOf())
        val expectedAiMessage = "I am an AI and I will recommend!"
        val expectedChatStream = TestTokenStream(expectedAiMessage)

        every { wsSession.attributes } returns mutableMapOf(
            "recommenderSession" to expectedRecommenderSession
        ) as Map<String, Any>
        every { discogsRecommenderService.chat(expectedRecommenderSession, inputString) } returns expectedChatStream
        every { wsSession.sendMessage(any()) } just runs

        wsRecommenderService.chat(inputString, wsSession)
        verify { wsSession.sendMessage(TextMessage(expectedAiMessage)) }
    }

    @Test
    fun `Should send an EOS when chat response ends`() {
        val inputString = "I want record recommendations!"
        val wsSession = mockk<WebSocketSession>()
        val expectedRecommenderSession = RecommenderSession(UUID.randomUUID(), DiscogsUser("gabriel"), listOf())
        val expectedAiMessage = "I am an AI and I will recommend!"
        val expectedChatStream = TestTokenStream(expectedAiMessage)

        every { wsSession.attributes } returns mutableMapOf(
            "recommenderSession" to expectedRecommenderSession
        ) as Map<String, Any>
        every { discogsRecommenderService.chat(expectedRecommenderSession, inputString) } returns expectedChatStream
        every { wsSession.sendMessage(any()) } just runs

        wsRecommenderService.chat(inputString, wsSession)
        verify { wsSession.sendMessage(TextMessage("EOS")) }
    }

    @Test
    fun `Should successfully start a websocket session`() {
        val session = mockk<WebSocketSession>()
        val expectedDiscogsUser = DiscogsUser("gabriel")
        val expectedRecommenderSession = RecommenderSession(UUID.randomUUID(), expectedDiscogsUser, listOf())
        val expectedHelloMessage = "Hello!"
        val expectedChatStream = TestTokenStream("Hello from AI!")

        every { session.uri } returns URI.create("ws://localhost:8080/chat?discogsUser=${expectedDiscogsUser.username}")
        every { session.attributes } returns mutableMapOf()
        every { session.sendMessage(any()) } just runs
        every { discogsRecommenderService.startRecommender(expectedDiscogsUser) } returns expectedRecommenderSession
        every { discogsRecommenderService.chat(expectedRecommenderSession, expectedHelloMessage) } returns expectedChatStream

        wsRecommenderService.setupSession(session)
        assertEquals(expectedRecommenderSession, session.attributes["recommenderSession"])
        verify { session.sendMessage(TextMessage("Hello from AI!")) }
    }
}
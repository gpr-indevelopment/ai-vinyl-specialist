package io.github.gprindevelopment.recommender

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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

    @ParameterizedTest
    @ValueSource(strings = ["ws://localhost:8080/chat", "ws://localhost:8080/chat/1234", "ws://localhost:8080/chat/1234?sessionid=1234"])
    fun `Should not start websocket without a session ID`() {
        val session = mockk<WebSocketSession>()

        every { session.uri } returns URI.create("ws://localhost:8080/chat")
        assertThrows<IllegalArgumentException> { wsRecommenderService.setupSession(session) }
    }

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
    fun `Should successfully start a websocket session`() {
        val session = mockk<WebSocketSession>()
        val expectedDiscogsUser = DiscogsUser("gabriel")
        val expectedSessionId = "1234"
        val expectedRecommenderSession = RecommenderSession(UUID.randomUUID(), expectedDiscogsUser, listOf())

        every { session.uri } returns URI.create("ws://localhost:8080/chat?sessionId=${expectedSessionId}&discogsUser=${expectedDiscogsUser.username}")
        every { session.attributes } returns mutableMapOf()
        every { discogsRecommenderService.startRecommender(expectedDiscogsUser) } returns expectedRecommenderSession

        wsRecommenderService.setupSession(session)
        assertEquals(expectedSessionId, session.attributes["sessionId"])
        assertEquals(expectedRecommenderSession, session.attributes["recommenderSession"])
    }
}
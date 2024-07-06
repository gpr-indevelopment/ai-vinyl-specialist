package io.github.gprindevelopment.recommender.server

import io.github.gprindevelopment.recommender.assistant.OpenAICostCalculator
import io.github.gprindevelopment.recommender.assistant.OpenAIVinylRecommenderAssistant
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.net.URI
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class WsRecommenderServiceTest {

    @InjectMockKs
    private lateinit var wsRecommenderService: WsRecommenderService

    @MockK
    private lateinit var assistant: OpenAIVinylRecommenderAssistant

    @MockK
    private lateinit var openAICostCalculator: OpenAICostCalculator

    @Test
    fun `Should chat through websockets`() {
        val inputString = "I want record recommendations!"
        val wsSession = mockk<WebSocketSession>()
        val memoryId = UUID.randomUUID()
        val expectedRecommenderSession = RecommenderSession(memoryId)
        val expectedAiMessage = "I am an AI and I will recommend!"
        val expectedChatStream = TestTokenStream(expectedAiMessage)

        every { wsSession.attributes } returns mutableMapOf(
            "recommenderSession" to expectedRecommenderSession
        ) as Map<String, Any>
        every { assistant.chat(inputString, memoryId) } returns expectedChatStream
        every { wsSession.sendMessage(any()) } just runs
        every { openAICostCalculator.calculateCostDollars(any()) } returns 0.0

        wsRecommenderService.chat(inputString, wsSession)
        verify { wsSession.sendMessage(TextMessage(expectedAiMessage)) }
    }

    @Test
    fun `Should send an EOS when chat response ends`() {
        val inputString = "I want record recommendations!"
        val wsSession = mockk<WebSocketSession>()
        val memoryId = UUID.randomUUID()
        val expectedRecommenderSession = RecommenderSession(memoryId)
        val expectedAiMessage = "I am an AI and I will recommend!"
        val expectedChatStream = TestTokenStream(expectedAiMessage)

        every { wsSession.attributes } returns mutableMapOf(
            "recommenderSession" to expectedRecommenderSession
        ) as Map<String, Any>
        every { assistant.chat(inputString, memoryId) } returns expectedChatStream
        every { wsSession.sendMessage(any()) } just runs
        every { openAICostCalculator.calculateCostDollars(any()) } returns 0.0

        wsRecommenderService.chat(inputString, wsSession)
        verify { wsSession.sendMessage(TextMessage("EOS")) }
    }

    @Test
    fun `Should successfully start a websocket session`() {
        val session = mockk<WebSocketSession>()
        val expectedHelloMessage = "Hello!"
        val expectedChatStream = TestTokenStream("Hello from AI!")
        val memoryIdSlot = slot<UUID>()

        every { session.uri } returns URI.create("ws://localhost:8080/chat")
        every { session.attributes } returns mutableMapOf()
        every { session.sendMessage(any()) } just runs
        every { assistant.chat(eq(expectedHelloMessage), capture(memoryIdSlot)) } returns expectedChatStream
        every { openAICostCalculator.calculateCostDollars(any()) } returns 0.0

        wsRecommenderService.setupSession(session)
        assertEquals(memoryIdSlot.captured, (session.attributes["recommenderSession"] as RecommenderSession).memoryId)
        verify { session.sendMessage(TextMessage("Hello from AI!")) }
    }
}
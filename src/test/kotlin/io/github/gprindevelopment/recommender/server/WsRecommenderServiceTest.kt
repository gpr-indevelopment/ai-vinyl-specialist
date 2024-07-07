package io.github.gprindevelopment.recommender.server

import com.fasterxml.jackson.databind.ObjectMapper
import dev.langchain4j.model.output.TokenUsage
import dev.langchain4j.service.Result
import io.github.gprindevelopment.recommender.assistant.OpenAICostCalculator
import io.github.gprindevelopment.recommender.assistant.OpenAIVinylRecommenderAssistant
import io.github.gprindevelopment.recommender.assistant.RecommenderResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.net.URI
import java.util.*
import kotlin.test.assertNotNull

@ExtendWith(MockKExtension::class)
class WsRecommenderServiceTest {

    @InjectMockKs
    private lateinit var wsRecommenderService: WsRecommenderService

    @MockK
    private lateinit var assistant: OpenAIVinylRecommenderAssistant

    @MockK
    private lateinit var openAICostCalculator: OpenAICostCalculator

    @SpyK
    var objectMapper: ObjectMapper = ObjectMapper()

    @Test
    fun `Should chat through websockets with json responses`() {
        val inputString = "I want record recommendations!"
        val wsSession = mockk<WebSocketSession>()
        val memoryId = UUID.randomUUID()
        val expectedRecommenderSession = RecommenderSession(memoryId)
        val expectedAiMessage = "I am an AI and I will recommend!"
        val expectedResult = Result.builder<RecommenderResponse>()
            .tokenUsage(TokenUsage(0, 0))
            .content(RecommenderResponse(expectedAiMessage, emptyList()))
            .build()

        every { wsSession.attributes } returns mutableMapOf(
            "recommenderSession" to expectedRecommenderSession
        ) as Map<String, Any>
        every { assistant.chatSync(inputString, memoryId) } returns expectedResult
        every { wsSession.sendMessage(TextMessage("{\"message\":\"I am an AI and I will recommend!\",\"recommendations\":[]}")) } just runs
        every { openAICostCalculator.calculateCostDollars(any()) } returns 0.0

        wsRecommenderService.chat(inputString, wsSession)
    }

    @Test
    fun `Should successfully start a websocket session with json responses`() {
        val session = mockk<WebSocketSession>()

        every { session.uri } returns URI.create("ws://localhost:8080/chat")
        every { session.attributes } returns mutableMapOf()

        wsRecommenderService.setupSession(session)
        assertNotNull((session.attributes["recommenderSession"] as RecommenderSession).memoryId)
    }
}
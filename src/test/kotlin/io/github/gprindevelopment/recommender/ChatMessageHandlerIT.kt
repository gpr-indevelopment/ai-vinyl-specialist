package io.github.gprindevelopment.recommender

import com.ninjasquad.springmockk.MockkBean
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.model.output.Response
import dev.langchain4j.service.OnCompleteOrOnError
import dev.langchain4j.service.OnError
import dev.langchain4j.service.OnStart
import dev.langchain4j.service.TokenStream
import io.mockk.every
import io.mockk.mockk
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
import java.util.function.Consumer
import kotlin.test.assertContains
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatMessageHandlerIT {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var chatMessageHandler: ChatMessageHandler;

    @MockkBean
    lateinit var assistant: BasicAssistant

    @MockkBean
    lateinit var discogsVinylRecommenderService: DiscogsVinylRecommenderService

    @Test
    fun `Should successfully communicate through websockets`() {
        val inputMessage = "Hello!"
        val expectedResponse = "Hello back!"
        val handler = TestWsHandler()
        every { assistant.chat(inputMessage, any()) } returns TestTokenStream(expectedResponse)
        every { discogsVinylRecommenderService.startRecommender(any()) } returns mockk()

        val session = StandardWebSocketClient().execute(handler, "ws://localhost:$port/chat?sessionId=1234&discogsUser=gabriel")
            .join()
        session.sendMessage(TextMessage(inputMessage))
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

    private class TestTokenStream(val message: String): TokenStream, OnCompleteOrOnError, OnStart, OnError {

        var onCompleteCallback: Consumer<Response<AiMessage>>? = null

        override fun onNext(tokenHandler: Consumer<String>?): OnCompleteOrOnError {
            return this
        }

        override fun onComplete(completionHandler: Consumer<Response<AiMessage>>?): OnError {
            this.onCompleteCallback = completionHandler
            return this
        }

        override fun ignoreErrors(): OnStart {
            return this
        }

        override fun onError(errorHandler: Consumer<Throwable>?): OnStart {
            return this
        }

        override fun start() {
            onCompleteCallback?.accept(Response(AiMessage(message)))
        }
    }
}
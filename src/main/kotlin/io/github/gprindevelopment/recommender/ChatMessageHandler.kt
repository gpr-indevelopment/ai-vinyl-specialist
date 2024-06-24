package io.github.gprindevelopment.recommender

import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Component
class ChatMessageHandler(val assistant: BasicAssistant): TextWebSocketHandler() {

    //TODO: Can these illegal argument exceptions become custom websocket exceptions?
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val uri = session.uri ?: throw IllegalArgumentException("URI is required.")
        val sessionId = extractSessionId(uri) ?: throw IllegalArgumentException("Session ID is required.")
        super.afterConnectionEstablished(session)
        session.attributes["sessionId"] = sessionId
        session.sendMessage(TextMessage("WebSocket connection is accepting requests."))
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        assistant.chat(message.payload)
            .onNext {}
            .onComplete { modelResponse -> session.sendMessage(TextMessage(modelResponse.content().text())) }
            .ignoreErrors()
            .start()
    }

    fun extractSessionId(uri: URI): String? {
        return UriComponentsBuilder
            .fromUri(uri)
            .build()
            .queryParams
            .getFirst("sessionId")
    }
}
package io.github.gprindevelopment.recommender

import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatMessageHandler(val assistant: VinylRecommenderAssistant): TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        super.afterConnectionEstablished(session)
        session.sendMessage(TextMessage("WebSocket connection is accepting requests."))
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        assistant.chat(message.payload)
            .onNext {}
            .onComplete { modelResponse -> session.sendMessage(TextMessage(modelResponse.content().text())) }
            .ignoreErrors()
            .start()
    }
}
package io.github.gprindevelopment.recommender

import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatMessageHandler(
    val service: WsRecommenderService): TextWebSocketHandler() {

    //TODO: Can these illegal argument exceptions become custom websocket exceptions?
    override fun afterConnectionEstablished(session: WebSocketSession) {
        service.setupSession(session)
        super.afterConnectionEstablished(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        service.chat(message.payload, session)
    }
}
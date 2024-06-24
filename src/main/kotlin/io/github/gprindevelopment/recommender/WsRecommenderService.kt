package io.github.gprindevelopment.recommender

import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Service
class WsRecommenderService {

    fun setupSession(wsSession: WebSocketSession) {
        val uri = wsSession.uri ?: throw IllegalArgumentException("URI is required.")
        val sessionId = extractSessionId(uri) ?: throw IllegalArgumentException("Session ID is required.")
        wsSession.attributes["sessionId"] = sessionId
    }

    private fun extractSessionId(uri: URI): String? {
        return UriComponentsBuilder
            .fromUri(uri)
            .build()
            .queryParams
            .getFirst("sessionId")
    }
}
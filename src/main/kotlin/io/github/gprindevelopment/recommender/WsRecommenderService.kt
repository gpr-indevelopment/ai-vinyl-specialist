package io.github.gprindevelopment.recommender

import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Service
class WsRecommenderService(
    val discogsRecommenderService: DiscogsVinylRecommenderService,
) {

    //TODO: Is sessionID really necessary? Maybe the session itself can hold the recommender session
    fun setupSession(wsSession: WebSocketSession) {
        val uri = wsSession.uri ?: throw IllegalArgumentException("URI is required.")
        val sessionId = extractSessionId(uri) ?: throw IllegalArgumentException("Session ID is required.")
        wsSession.attributes["sessionId"] = sessionId
        val discogsUser = extractDiscogsUsername(uri) ?: throw IllegalArgumentException("Discogs user is required.")
        val recommenderSession = discogsRecommenderService.startRecommender(DiscogsUser(discogsUser))
        wsSession.attributes["recommenderSession"] = recommenderSession
    }

    private fun extractDiscogsUsername(uri: URI): String? {
        return UriComponentsBuilder
            .fromUri(uri)
            .build()
            .queryParams
            .getFirst("discogsUser")
    }

    private fun extractSessionId(uri: URI): String? {
        return UriComponentsBuilder
            .fromUri(uri)
            .build()
            .queryParams
            .getFirst("sessionId")
    }
}
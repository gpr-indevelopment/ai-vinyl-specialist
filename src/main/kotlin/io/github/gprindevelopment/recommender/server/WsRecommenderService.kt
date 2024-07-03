package io.github.gprindevelopment.recommender.server

import io.github.gprindevelopment.recommender.discogs.DiscogsUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Service
class WsRecommenderService(
    val discogsRecommenderService: DiscogsVinylRecommenderService,
) {

    private val logger = LoggerFactory.getLogger(WsRecommenderService::class.java)

    fun setupSession(wsSession: WebSocketSession) {
        val uri = wsSession.uri ?: throw IllegalArgumentException("URI is required.")
        val discogsUser = extractDiscogsUsername(uri) ?: throw IllegalArgumentException("Discogs user is required.")
        val recommenderSession = discogsRecommenderService.startRecommender(DiscogsUser(discogsUser))
        wsSession.attributes["recommenderSession"] = recommenderSession
        chat("Hello!", wsSession, recommenderSession)
    }

    fun chat(message: String, wsSession: WebSocketSession) {
        val recommenderSession = wsSession.attributes["recommenderSession"] as RecommenderSession
        chat(message, wsSession, recommenderSession)
    }

    private fun chat(message: String, wsSession: WebSocketSession, recommenderSession: RecommenderSession) {
        val stream = discogsRecommenderService.chat(recommenderSession, message)
        //TODO: What to do with errors?
        stream
            .onNext { modelResponse -> wsSession.sendMessage(TextMessage(modelResponse)) }
            .onComplete {
                logger.info("Completed assistant response. ${it.tokenUsage()}")
                wsSession.sendMessage(TextMessage("EOS"))
            }
            .ignoreErrors()
            .start()
    }

    private fun extractDiscogsUsername(uri: URI): String? {
        return UriComponentsBuilder
            .fromUri(uri)
            .build()
            .queryParams
            .getFirst("discogsUser")
    }
}
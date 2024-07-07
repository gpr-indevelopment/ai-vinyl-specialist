package io.github.gprindevelopment.recommender.server

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.gprindevelopment.recommender.assistant.OpenAICostCalculator
import io.github.gprindevelopment.recommender.assistant.OpenAIVinylRecommenderAssistant
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.*

@Service
class WsRecommenderService(
    val assistant: OpenAIVinylRecommenderAssistant,
    val openAICostCalculator: OpenAICostCalculator,
    val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(WsRecommenderService::class.java)

    //TODO: Can these illegal argument exceptions become custom websocket exceptions?
    fun setupSession(wsSession: WebSocketSession) {
        val recommenderSession = RecommenderSession(UUID.randomUUID())
        wsSession.attributes["recommenderSession"] = recommenderSession
    }

    fun chat(message: String, wsSession: WebSocketSession) {
        val recommenderSession = wsSession.attributes["recommenderSession"] as RecommenderSession
        chat(message, wsSession, recommenderSession)
    }

    private fun chat(message: String, wsSession: WebSocketSession, recommenderSession: RecommenderSession) {
        val chatResult = assistant.chatSync(message, recommenderSession.memoryId)
        logger.info("Completed assistant response. Costs $ ${openAICostCalculator.calculateCostDollars(chatResult.tokenUsage())}. ${chatResult.tokenUsage()}")
        wsSession.sendMessage(TextMessage(objectMapper.writeValueAsString(chatResult.content())))
        //TODO: What to do with errors?
    }
}
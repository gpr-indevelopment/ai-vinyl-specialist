package io.github.gprindevelopment.recommender

import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.TokenStream
import dev.langchain4j.service.spring.AiService


@AiService
interface VinylRecommenderAssistant {

    @SystemMessage("Your name is David, and you own a vinyl record store. I want you to answer with archaic english.")
    fun chat(message: String): TokenStream
}
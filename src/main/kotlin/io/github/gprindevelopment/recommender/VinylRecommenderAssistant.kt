package io.github.gprindevelopment.recommender

import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.TokenStream
import dev.langchain4j.service.UserMessage
import dev.langchain4j.service.spring.AiService
import java.util.*


@AiService
interface VinylRecommenderAssistant {

    @SystemMessage("Your name is David, and you own a vinyl record store. I want you to answer with archaic english.")
    fun chat(@UserMessage message: String, @MemoryId memoryId: UUID = UUID.randomUUID()): TokenStream

    @SystemMessage("Your name is David, and you own a vinyl record store. I want you to answer with archaic english.")
    fun chatSync(@UserMessage message: String, @MemoryId memoryId: UUID = UUID.randomUUID()): String
}
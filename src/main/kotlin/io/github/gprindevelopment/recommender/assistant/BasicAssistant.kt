package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.TokenStream
import dev.langchain4j.service.UserMessage
import dev.langchain4j.service.spring.AiService
import java.util.*

@AiService
interface BasicAssistant {

    fun chat(@UserMessage message: String, @MemoryId memoryId: UUID = UUID.randomUUID()): TokenStream

    fun chatSync(@UserMessage message: String, @MemoryId memoryId: UUID = UUID.randomUUID()): String
}
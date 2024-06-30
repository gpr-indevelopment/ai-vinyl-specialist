package io.github.gprindevelopment.recommender.assistant.openai

import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.TokenStream
import dev.langchain4j.service.UserMessage
import dev.langchain4j.service.spring.AiService
import dev.langchain4j.service.spring.AiServiceWiringMode
import java.util.*

@AiService(
    wiringMode = AiServiceWiringMode.EXPLICIT,
    streamingChatModel = "openAiStreamingChatModel",
    chatMemoryProvider = "chatMemoryProvider"
)
interface OpenAIBasicAssistant {

    fun chat(@UserMessage message: String, @MemoryId memoryId: UUID = UUID.randomUUID()): TokenStream
}
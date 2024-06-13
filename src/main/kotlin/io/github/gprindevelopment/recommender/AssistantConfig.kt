package io.github.gprindevelopment.recommender

import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AssistantConfig {

    @Bean
    fun chatMemoryProvider(): ChatMemoryProvider {
        return ChatMemoryProvider { MessageWindowChatMemory.withMaxMessages(10) }
    }
}
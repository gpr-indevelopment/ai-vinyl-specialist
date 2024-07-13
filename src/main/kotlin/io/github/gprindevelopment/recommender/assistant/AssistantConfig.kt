package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever
import dev.langchain4j.web.search.WebSearchEngine
import dev.langchain4j.web.search.google.customsearch.GoogleCustomWebSearchEngine
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AssistantConfig {

    @Bean
    fun chatMemoryProvider(): ChatMemoryProvider {
        return ChatMemoryProvider { MessageWindowChatMemory.withMaxMessages(10) }
    }

    @Bean
    fun googleSearchContentRetriever(
        @Value("\${google.apiKey}") apiKey: String,
        @Value("\${google.searchEngineId}") searchEngineId: String,
        @Value("\${google.logRequests}") logRequests: Boolean
    ): ContentRetriever {
        val googleSearchEngine: WebSearchEngine = GoogleCustomWebSearchEngine.builder()
            .apiKey(apiKey)
            .csi(searchEngineId)
            .logRequests(logRequests)
            .build()
        return WebSearchContentRetriever.builder()
            .webSearchEngine(googleSearchEngine)
            .maxResults(3)
            .build()
    }
}
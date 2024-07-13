package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.rag.content.Content
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever
import dev.langchain4j.rag.query.Query
import org.slf4j.LoggerFactory

class ErrorProofWebSearchContentRetriever(
    val delegate: WebSearchContentRetriever
): ContentRetriever {

    private val logger = LoggerFactory.getLogger(ErrorProofWebSearchContentRetriever::class.java)

    override fun retrieve(query: Query?): MutableList<Content> {
        try {
            return delegate.retrieve(query)
        } catch (e: Exception) {
            logger.warn("Error ignored while retrieving search content", e)
            return mutableListOf()
        }
    }
}
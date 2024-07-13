package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import io.github.gprindevelopment.recommender.discogs.DiscogsService
import io.github.gprindevelopment.recommender.discogs.DiscogsUser
import io.github.gprindevelopment.recommender.discogs.SimpleVinylRecord
import org.springframework.stereotype.Component

@Component
class DiscogsToolsProvider(
    val discogsService: DiscogsService
) {

    @Tool("Searches Discogs for the full vinyl records collection for a given username")
    fun fetchFullVinylRecordCollection(@P("Discogs username") discogsUsername: String): List<SimpleVinylRecord> {
        return discogsService.getFullCollection(DiscogsUser(discogsUsername))
    }
}
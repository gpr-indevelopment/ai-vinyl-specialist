package io.github.gprindevelopment.recommender.assistant

import io.github.gprindevelopment.recommender.discogs.DiscogsService
import io.github.gprindevelopment.recommender.discogs.DiscogsUser
import io.github.gprindevelopment.recommender.domain.VinylRecord
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class ToolsProviderTest {

    @InjectMockKs
    private lateinit var toolsProvider: ToolsProvider

    @MockK
    private lateinit var discogsService: DiscogsService

    @Test
    fun `Tool should fetch full vinyl record collection`() {
        val discogsUsername = "test"
        val expected = emptyList<VinylRecord>()

        every { discogsService.getFullCollection(DiscogsUser(discogsUsername)) } returns expected

        val actual = toolsProvider.fetchFullVinylRecordCollection(discogsUsername)
        assertEquals(expected, actual)
        verify { discogsService.getFullCollection(DiscogsUser(discogsUsername)) }
    }
}
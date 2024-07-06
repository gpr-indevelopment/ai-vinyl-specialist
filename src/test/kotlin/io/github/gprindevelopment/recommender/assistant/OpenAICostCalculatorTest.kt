package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.model.output.TokenUsage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OpenAICostCalculatorTest {

    @Test
    fun `Should calculate cost in dollars`() {
        val costCalculator = OpenAICostCalculator(0.5, 1.5)
        val tokenUsage = TokenUsage(4000, 35)
        val cost = costCalculator.calculateCostDollars(tokenUsage)
        assertEquals(0.002 + 0.0000525, cost)
    }

    @Test
    fun `Should calculate cost as 0 when token usage is zero`() {
        val costCalculator = OpenAICostCalculator(0.5, 1.5)
        val tokenUsage = TokenUsage(0, 0)
        val cost = costCalculator.calculateCostDollars(tokenUsage)
        assertEquals(0.0, cost)
    }

    @Test
    fun `Should calculate cost as 0 when input and output costs are zero`() {
        val costCalculator = OpenAICostCalculator(0.0, 0.0)
        val tokenUsage = TokenUsage(4000, 35)
        val cost = costCalculator.calculateCostDollars(tokenUsage)
        assertEquals(0.0, cost)
    }
}
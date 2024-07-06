package io.github.gprindevelopment.recommender.assistant

import dev.langchain4j.model.output.TokenUsage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class OpenAICostCalculator(
    @Value("\${assistant.openai.inputTokenCostPerMillion}") val inputTokenCostPerMillion: Double,
    @Value("\${assistant.openai.outputTokenCostPerMillion}") val outputTokenCostPerMillion: Double
) {

    //TODO: Adjust decimal places, do rounding. Example 4.4399999999999995E-4
    fun calculateCostDollars(tokenUsage: TokenUsage): Double {
        return calculateInputCost(tokenUsage) + calculateOutputCost(tokenUsage)
    }

    private fun calculateInputCost(tokenUsage: TokenUsage): Double {
        return inputTokenCostPerMillion * tokenUsage.inputTokenCount() / 1_000_000
    }

    private fun calculateOutputCost(tokenUsage: TokenUsage): Double {
        return outputTokenCostPerMillion * tokenUsage.outputTokenCount() / 1_000_000
    }
}
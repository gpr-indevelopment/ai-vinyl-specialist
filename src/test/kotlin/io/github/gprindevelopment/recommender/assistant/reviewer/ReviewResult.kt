package io.github.gprindevelopment.recommender.assistant.reviewer

data class ReviewResult(
    val answer: Boolean,
    val reasoning: String
)
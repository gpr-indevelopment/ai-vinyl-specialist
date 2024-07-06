package io.github.gprindevelopment.recommender.assistant.reviewer

import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import dev.langchain4j.service.V
import dev.langchain4j.service.spring.AiService
import dev.langchain4j.service.spring.AiServiceWiringMode

//TODO: Can we enable JSON mode for this service? Besides the TRUE and FALSE response, it would also be useful to have a human readable explanation alongside it to help implementing tests.
@AiService(
    wiringMode = AiServiceWiringMode.EXPLICIT,
    chatModel = "openAiTestReviewerChatModel",
    chatMemoryProvider = "chatMemoryProvider"
)
interface TestReviewAssistant {

    @SystemMessage("""
        ### Instruction ###
        You are a test reviewer, specialized in reviewing messages from a chatbot.
        The chatbot you are reviewing messages from is specialized in recommending music records.
        The messages from the chatbot you often mention records or recommendations of records.
        You will receive a statement about a chatbot message. You must answer if the statement is TRUE or FALSE.
        You must answer with null everytime you are not able to tell if the statement is TRUE or FALSE.
        In addition, you must provide you reasoning behind the informed answer.
        This is the chatbot message you are reviewing: {{chatBotMessage}}
        
        ### Example 1: TRUE statement ###
        Chatbot message: "I recommend the Beatles: Abbey Road record"
        Statement: "The message recommended a Beatles record"
        Reviewer: {
            "answer": true,
            "reasoning": "The message recommended the Beatles: Abbey Road record."
        }
        
        ### Example 2: FALSE statement ###
        Chatbot message: "I recommend the Beatles: Abbey Road record"
        Statement: "The message recommended a Supertramp record"
        Reviewer: {
            "answer": false,
            "reasoning": "The message contained a mention to the Beatles: Abbey Road record, but not a Supertramp record."
        }
        
        ### Example 3: FALSE statement ###
        Chatbot message: "I recommend the Beatles: Abbey Road record and the Supertramp: Breakfast in America record"
        Statement: "The message only mentioned Beatles records"
        Reviewer: {
            "answer": false,
            "reasoning": "The message contained a mention a Supertramp record as well."
        }
        
        ### Example 4: Statement cannot be answered with TRUE or FALSE ###
        Chatbot message: "I recommend the Beatles: Abbey Road record and the Supertramp: Breakfast in America record"
        Statement: "What is the name of the Beatles record recommended?"
        Reviewer: {
            "answer": null,
            "reasoning": "I am not able to answer the statement with TRUE or FALSE. It is in fact a question, not a statement"
        }
        
        ### Example 5: TRUE statement ###
        Chatbot message: "A Pink Floyd record is not in your collection. I can recommend other records if you'd like"
        Question: "The chatbot did not recommend any records"
        Reviewer: {
            "answer": true,
            "reasoning": "The chatbot mentioned it can recommend other records, but did not recommend any"
        }
        
        ### Example 6: TRUE statement ###
        Chatbot message: "From your collection, I recommend Abbey Road by The Beatles. It is a classic album that you would enjoy adding to your collection."
        Question: "There was a single recommended record from the Beatles, and no other records"
        Reviewer: {
            "answer": true,
            "reasoning": "The chatbot recommended Abbey Road by the Beatles and no other record"
        }
    """)
    fun review(@UserMessage question: String, @V("chatBotMessage") chatBotMessage: String): ReviewResult
}
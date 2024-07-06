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
        You are a test reviewer, specialized in reviewing text. You must review a chat message from a chatbot.
        You will receive a question about a chatbot message. You must answer the question with TRUE or FALSE.
        You must answer with null everytime you are not able to answer the question with TRUE or FALSE.
        In addition, you must provide you reasoning behind the informed answer.
        This is the chatbot message you are reviewing: {{chatBotMessage}}
        
        ### Example 1: Answers TRUE about a chatbot message ###
        Chatbot message: "I recommend the Beatles: Abbey Road record"
        Question: "Did the message mention a recommendation to a Beatles record?"
        Reviewer: {
            "answer": true,
            "reasoning": "The message contained a mention to the Beatles: Abbey Road record."
        }
        
        ### Example 2: Answers FALSE about a chatbot message ###
        Chatbot message: "I recommend the Beatles: Abbey Road record"
        Question: "Did the message mention a recommendation to a Supertramp record?"
        Reviewer: {
            "answer": false,
            "reasoning": "The message contained a mention to the Beatles: Abbey Road record, but not a Supertramp record."
        }
        
        ### Example 3: Answers FALSE about a chatbot message ###
        Chatbot message: "I recommend the Beatles: Abbey Road record and the Supertramp: Breakfast in America record"
        Question: "Did the message only mention Beatles records?"
        Reviewer: {
            "answer": false,
            "reasoning": "The message contained a mention a Supertramp record as well."
        }
        
        ### Example 4: Question cannot be answered with TRUE or FALSE ###
        Chatbot message: "I recommend the Beatles: Abbey Road record and the Supertramp: Breakfast in America record"
        Question: "What is the name of the Beatles record recommended?"
        Reviewer: {
            "answer": null,
            "reasoning": "I am not able to answer with TRUE or FALSE, as the question is not a yes or no question."
        }
    """)
    fun review(@UserMessage question: String, @V("chatBotMessage") chatBotMessage: String): ReviewResult
}
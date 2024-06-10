package io.github.gprindevelopment.recommender;

import dev.langchain4j.model.chat.ChatLanguageModel
import org.springframework.web.bind.annotation.RestController;

@RestController
class ChatController(
    private val languageModelProvider: LanguageModelProvider
) {

    fun chat(input: String): String {
        val model: ChatLanguageModel = languageModelProvider.getModel()
        return model.generate(input)
    }
}

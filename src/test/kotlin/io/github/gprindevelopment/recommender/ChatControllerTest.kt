package io.github.gprindevelopment.recommender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension;
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class ChatControllerTest {

    @InjectMocks
    lateinit var chatController: ChatController

    val testLanguageModelProvider = TestLanguageModelProvider()

    @Spy
    val languageModelProvider: LanguageModelProvider = testLanguageModelProvider.startProvider()

    @Test
    fun Should_chat_with_llama_3() {
        val input = "Hello! Please say hello back, and include the word 'Hello' within your response"
        val output = chatController.chat(input)
        assertTrue(output.contains("Hello"))
        // TODO: Make is so that tests dont have to do teardown on their own
        testLanguageModelProvider.stopProvider()
    }
}
package io.github.gprindevelopment.recommender

import org.testcontainers.containers.GenericContainer
import org.testcontainers.ollama.OllamaContainer
import org.testcontainers.utility.DockerImageName

// TODO: Improve how this test model provider works, make it seamless to test runs and teardown correctly
class TestLanguageModelProvider {

    var ollama: OllamaContainer = OllamaContainer(
        DockerImageName.parse("langchain4j/ollama-llama3:latest")
            .asCompatibleSubstituteFor("ollama/ollama")
    )

    fun startProvider(): LanguageModelProvider {
        ollama.start()
        val baseUrl = baseUrl(ollama)
        return LanguageModelProvider("llama3", baseUrl)
    }

    fun stopProvider() {
        ollama.stop()
    }

    private fun baseUrl(ollama: GenericContainer<*>): String {
        return String.format("http://%s:%d", ollama.host, ollama.firstMappedPort)
    }
}
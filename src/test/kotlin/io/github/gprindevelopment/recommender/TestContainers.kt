package io.github.gprindevelopment.recommender

import org.testcontainers.ollama.OllamaContainer
import org.testcontainers.utility.DockerImageName

object TestContainers {

    val ollamaContainer: OllamaContainer = OllamaContainer(
        //TODO: Can we make the modelName property come from the properties file?
        //TODO: How to get rid of hanging containers?
        DockerImageName.parse("langchain4j/ollama-llama3:latest")
            .asCompatibleSubstituteFor("ollama/ollama")
    ).withReuse(true)

    fun OllamaContainer.containerBaseUrl(): String {
        return String.format("http://%s:%d", this.host, this.firstMappedPort)
    }
}
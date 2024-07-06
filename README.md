# David's vinyl record store

David is the owner of a vinyl records shop. Your Discogs collection is his shop's stock of vinyl records.

You can ask him for a recommendation or for additional information about any of the records in stock. 
David will be happy to help you.

## Technical details

This LLM application is a simple example of a conversational agent that uses the Discogs API to provide information about vinyl records.
It consists of 4 main building blocks:

1. **Discogs API client**: Given a username, it retrieves the collection of vinyl records from the Discogs API.
2. **AI assistant engine (David)**: Given a record collection, it provides a conversational interface about the records.
3. **Websockets server**: It provides a websockets server to interact with the AI assistant.
4. **User interface**: It provides a simple user interface to interact with the AI assistant through websockets.

It currently uses the [LangChain4j](https://docs.langchain4j.dev) as framework with a local [llama3 from Ollama](https://ollama.com/library/llama3) platform as the AI assistant engine, but it can be easily adapted to use other engines.

The application architecture from below diagram is enforced by the ArchUnit framework through tests in the [ArchitectureTest](src/test/kotlin/io/github/gprindevelopment/recommender/ArchitectureTest.kt) class.

![Architecture diagram](assets/vinyl-recommender-architecture.png)

## Challenges and limitations

* The llama3 model currently has no support for tools (June 2024). This means that the AI assistant cannot collect the Discogs username and retrieve the record collection on its own. We must do it on the application side and give the data as a system prompt to the model.
* LLMs might hallucinate. David has provided some incorrect information about records. I am evaluating a [RAG](https://docs.langchain4j.dev/tutorials/rag) approach for minimizing this scenario.
* Running these models locally can be slow. I have noticed significant cold start times for starting a conversation with David. I am investigating ways of improving cold starts, or moving to a cloud-based model like [Gemini](https://gemini.google.com/) or [ChatGPT](https://chatgpt.com).

## Lessons learned

The UI for this project was developed using the following prompt:

```
I need the code for an HTML 5 page that contains an input field for a Discogs username 
and a text area for inserting prompts for the application to send to AI agents. 
Above the text area there should be the space in which the AI responses are displayed, in the ChatGPT style
```

The generated HTML5 code from GitHub Copilot gave the initial visuals for the UI 
which I then modified to add the websocket connection and the logic to send and receive messages from the AI assistant.
I found this to be a very quick approach to prototyping.

Some LLMs are not as smart as others. Even though Mistral 7b model supports tools, 
I was not able to get good answers from it. It would not even pass my integration tests.
With that, I was not able to run an LLm with tools free of charge.

Hallucinations are a pain. I am beginning my journey into RAG as a way to minimize it.
Since David operates in the domain of music, Wikipedia is the first knowledge base that comes to mind for RAG.
Maybe I can leverage the MediaWiki API for searching music pages that are relevant to the conversation.

Testing the LLM application was a challenge. I did more integration tests than usual. This led to a slower development cycle.
Also, the probabilistic nature of the AI assistant makes it hard to test the application in a deterministic way.
I was always waiting for the time in which the assistant would not respond what I expected. Fortunately, it behaves as expected more often than not.

## Go right/wrong

1. Go right: Few-shot prompting. The results from the recommender improved significantly when I applied this technique to my system prompt. I added the persona, the tone, some context behind the conversation and a few examples on what would be expected from the AI to do.
2. Go wrong: AI test reviewer. Assertions from the AI integration tests often involved string processing. I thought using another AI specialized in reviewing test outcomes would facilitate the process. I would give it a statement and the response from the main AI, and the test reviwer would return if its a pass or fail. It led to flaky tests due to the accumulation of probabilistic errors of both AIs working together.
3. Go wrong: Using Ollama and llama3. I initially used llama3 running inside Ollama as a way to prototype a gen AI application for free. It worked initially, but it quickly led to very slow development cycles. My test suite would take 5 minutes to run with 20 tests while running a reusable test container. Running the application had a ~1 minute cold start delay for starting the conversation. I had to substitute it for a cloud-based LLM like ChatGPT 3.5.
4. Go right: Tools. I initially fetched the collection of vinyl records from Discogs using application code and added it to the system prompt of the LLM. This approach required an input field on the UI for collecting this data. When changing to tools, the AI would be able to collect the Discogs username from the user on its own, and make it all more conversational. It eliminates the need for UI forms.

## How to run

You must have [llama3 from Ollama](https://ollama.com/library/llama3) installed and running locally to run this application.
Run `ollama run llama3` to start a conversation with llama3 locally through the CLI. If that works, then the model API should also be available for the application.

1. Clone the repository.
2. Run `./gradlew bootRun` to start the application.
3. Visit `http://localhost:8080` in your browser to interact with the AI assistant.


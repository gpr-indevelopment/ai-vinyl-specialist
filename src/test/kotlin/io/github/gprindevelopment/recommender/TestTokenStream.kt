package io.github.gprindevelopment.recommender

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.model.output.Response
import dev.langchain4j.service.OnCompleteOrOnError
import dev.langchain4j.service.OnError
import dev.langchain4j.service.OnStart
import dev.langchain4j.service.TokenStream
import java.util.function.Consumer

class TestTokenStream(val message: String): TokenStream, OnCompleteOrOnError, OnStart, OnError {

    var onCompleteCallback: Consumer<Response<AiMessage>>? = null

    var onNextCallback: Consumer<String>? = null

    override fun onNext(onNextHandler: Consumer<String>?): OnCompleteOrOnError {
        onNextCallback = onNextHandler
        return this
    }

    override fun onComplete(completionHandler: Consumer<Response<AiMessage>>?): OnError {
        this.onCompleteCallback = completionHandler
        return this
    }

    override fun ignoreErrors(): OnStart {
        return this
    }

    override fun onError(errorHandler: Consumer<Throwable>?): OnStart {
        return this
    }

    override fun start() {
        onNextCallback?.accept(message)
        onCompleteCallback?.accept(Response(AiMessage(message)))
    }
}
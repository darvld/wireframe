package io.github.darvld.wireframe.ktor

import graphql.ExceptionWhileDataFetching
import graphql.GraphQLException
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import graphql.execution.SimpleDataFetcherExceptionHandler
import java.util.concurrent.CompletableFuture

internal object DefaultExceptionHandler : SimpleDataFetcherExceptionHandler() {
    override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters): CompletableFuture<DataFetcherExceptionHandlerResult> {
        val exception = unwrap(handlerParameters.exception)
        val error = ExceptionWhileDataFetching(
            handlerParameters.path,
            exception,
            handlerParameters.sourceLocation
        )

        // Don't log "expected" exceptions
        if (exception !is GraphQLException) {
            logException(error, exception)
        }

        val result = DataFetcherExceptionHandlerResult.newResult()
            .error(error)
            .build()

        return CompletableFuture.completedFuture(result)
    }
}
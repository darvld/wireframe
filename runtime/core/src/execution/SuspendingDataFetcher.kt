package io.github.darvld.wireframe.execution

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.github.darvld.wireframe.routing.ResolverScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import java.util.concurrent.CompletableFuture

@JvmInline
public value class SuspendingDataFetcher<T>(
    private val resolver: suspend ResolverScope.() -> T
) : DataFetcher<CompletableFuture<T>> {
    override fun get(env: DataFetchingEnvironment): CompletableFuture<T> {
        return env.graphQlContext.scope.async { resolver(env) }.asCompletableFuture()
    }
}
package io.github.darvld.wireframe.execution

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.github.darvld.wireframe.WireframeInternal
import io.github.darvld.wireframe.routing.ResolverScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import java.util.concurrent.CompletableFuture

/**
 * A coroutine-based adapter for graphql-java's [DataFetcher], using the
 * [CoroutineScope][kotlinx.coroutines.CoroutineScope] assigned to each
 * request to run the resolver's coroutine.
 */
@JvmInline
@WireframeInternal
public value class SuspendingDataFetcher<T>(
    private val resolver: suspend ResolverScope.() -> T
) : DataFetcher<CompletableFuture<T>> {
    override fun get(env: DataFetchingEnvironment): CompletableFuture<T> {
        return env.graphQlContext.scope.async { resolver(ResolverScope(env)) }.asCompletableFuture()
    }
}
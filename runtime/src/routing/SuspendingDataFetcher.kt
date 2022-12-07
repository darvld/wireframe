package io.github.darvld.wireframe.routing

import graphql.GraphQLContext
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture

internal typealias SuspendingDataFetcher<T> = suspend ResolverScope.(DataFetchingEnvironment) -> T

internal fun suspendingDataFetcher(
    buildScope: (GraphQLContext) -> CoroutineScope,
    fetcher: SuspendingDataFetcher<Any?>
): DataFetcher<Any?> {
    return DataFetcher { env ->
        buildScope(env.graphQlContext).async { DefaultResolverScope(env).fetcher(env) }.asCompletableFuture()
    }
}
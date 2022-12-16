package io.github.darvld.wireframe.execution

import graphql.GraphQLContext
import kotlinx.coroutines.CoroutineScope

@PublishedApi
internal object ContextKeys {
    const val coroutineScope = "wireframe.execution.scope"
}

public inline var GraphQLContext.scope: CoroutineScope
    get() = get(ContextKeys.coroutineScope)
    internal set(value) {
        put(ContextKeys.coroutineScope, value)
    }
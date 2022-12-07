package io.github.darvld.wireframe.ktor

import graphql.GraphQLContext
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope

@PublishedApi
internal object ContextKeys {
    const val coroutineScope = "wireframe.ktor.scope"
    const val applicationCall = "wireframe.ktor.call"
}

public inline val GraphQLContext.scope: CoroutineScope
    get() = get(ContextKeys.coroutineScope)

public inline val GraphQLContext.call: ApplicationCall
    get() = get(ContextKeys.applicationCall)
package io.github.darvld.wireframe.execution

import graphql.GraphQLContext
import kotlinx.coroutines.CoroutineScope

@PublishedApi
internal object ContextKeys {
    const val coroutineScope = "wireframe.execution.scope"
}

internal fun GraphQLContext.useScope(scope: CoroutineScope): GraphQLContext = apply {
    check(get<CoroutineScope?>(ContextKeys.coroutineScope) == null) {
        "A coroutine scope has already been assigned to this context."
    }

    put(ContextKeys.coroutineScope, scope)
}

/**
 * The [CoroutineScope] in which the current request was launched.
 *
 * This scope is guaranteed to have a [SupervisorJob][kotlinx.coroutines.SupervisorJob] as
 * part of its context, which enables individual coroutines to fail within the scope, without
 * affecting the others.
 */
public inline val GraphQLContext.scope: CoroutineScope
    get() = get(ContextKeys.coroutineScope)
package io.github.darvld.wireframe.ktor

import graphql.GraphQLContext
import io.ktor.server.application.*

@PublishedApi
internal object KtorContext {
    const val applicationCall = "wireframe.ktor.call"
}

internal fun GraphQLContext.useCall(call: ApplicationCall) {
    check(get<ApplicationCall?>(KtorContext.applicationCall) == null) {
        "A Ktor call has already been assigned to this context."
    }

    put(KtorContext.applicationCall, call)
}


/**
 * The [ApplicationCall] for the current request, as provided by Ktor.
 *
 * This property should only be used to query Ktor-specific information,
 * such as HTTP headers, authorization data, etc.
 *
 * Do not respond to the call or otherwise mofify its state, as it will
 * lead to unexpected results.
 */
public inline val GraphQLContext.call: ApplicationCall
    get() = get(KtorContext.applicationCall)
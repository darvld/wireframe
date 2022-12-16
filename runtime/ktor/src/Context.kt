package io.github.darvld.wireframe.ktor

import graphql.GraphQLContext
import io.github.darvld.wireframe.execution.ContextPlugin
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

public typealias CallContext = PipelineContext<Unit, ApplicationCall>
public typealias KtorContextPlugin = ContextPlugin<CallContext>

@PublishedApi
internal object KtorContext {
    const val applicationCall = "wireframe.ktor.call"
}

public inline var GraphQLContext.call: ApplicationCall
    get() = get(KtorContext.applicationCall)
    internal set(value) {
        put(KtorContext.applicationCall, value)
    }
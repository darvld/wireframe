package io.github.darvld.wireframe.ktor

import graphql.GraphQLContext
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

public typealias CallContext = PipelineContext<Unit, ApplicationCall>

public fun interface ContextPlugin {
    public suspend fun CallContext.updateContext(context: GraphQLContext)
}
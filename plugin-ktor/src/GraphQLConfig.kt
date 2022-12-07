package io.github.darvld.wireframe.ktor

import graphql.GraphQLContext
import io.github.darvld.wireframe.routing.Resolvers
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

public typealias ResolverRouting = Resolvers.() -> Unit
public typealias ContextPlugin = PipelineContext<Unit, ApplicationCall>.(GraphQLContext.Builder) -> Unit

public class GraphQLConfig internal constructor() {
    internal var resolvers: ResolverRouting? = null
    internal var contextBuilder: ContextPlugin? = null
    internal var sdl: String = ""

    public fun sdl(schema: String) {
        sdl = schema
    }

    public fun resolvers(block: Resolvers.() -> Unit) {
        resolvers = block
    }

    public fun buildContext(build: ContextPlugin) {
        contextBuilder = build
    }
}
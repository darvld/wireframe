package io.github.darvld.wireframe.ktor

import io.github.darvld.wireframe.routing.Resolvers

public typealias ResolverRouting = Resolvers.() -> Unit

public class GraphQLConfig internal constructor() {
    internal var resolvers: ResolverRouting? = null
    internal var contextPlugins: Iterable<ContextPlugin>? = null
    internal var sdl: String = ""

    public fun sdl(schema: String) {
        sdl = schema
    }

    public fun resolvers(block: Resolvers.() -> Unit) {
        resolvers = block
    }

    public fun contextPlugins(vararg plugins: ContextPlugin) {
        contextPlugins = plugins.toList()
    }

    public fun contextPlugins(plugins: Iterable<ContextPlugin>) {
        contextPlugins = plugins
    }
}
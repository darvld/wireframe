package io.github.darvld.wireframe.ktor

import io.github.darvld.wireframe.routing.Resolvers
import io.github.darvld.wireframe.transport.JsonSerializer

public typealias ResolverRouting = Resolvers.() -> Unit

public class GraphQLConfig internal constructor() {
    private var _resolvers: ResolverRouting? = null
    private var _contextPlugins: List<KtorContextPlugin>? = null
    private var _sdl: String = ""
    private var _transport: JsonSerializer? = null

    internal val resolvers: Resolvers
        get() = _resolvers?.let(Resolvers()::apply)
            ?: throw IllegalStateException("Resolvers must be specified in the endpoint configuration.")

    internal val sdl: String
        get() {
            require(_sdl.isNotBlank()) { "SDL should not be blank" }
            return _sdl
        }

    internal val contextPlugins: List<KtorContextPlugin>
        get() = _contextPlugins ?: emptyList()

    internal val transport: JsonSerializer
        get() = _transport ?: throw IllegalStateException("Transport must be specified.")

    public fun sdl(schema: String) {
        _sdl = schema
    }

    public fun resolvers(block: Resolvers.() -> Unit) {
        _resolvers = block
    }

    public fun contextPlugins(vararg plugins: KtorContextPlugin) {
        _contextPlugins = plugins.toList()
    }

    public fun useSerializer(transport: JsonSerializer) {
        _transport = transport
    }

    public fun contextPlugins(plugins: Iterable<KtorContextPlugin>) {
        _contextPlugins = plugins.toList()
    }
}
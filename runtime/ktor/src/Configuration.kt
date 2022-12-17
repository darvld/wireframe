package io.github.darvld.wireframe.ktor

import io.github.darvld.wireframe.WireframeInternal
import io.github.darvld.wireframe.routing.Resolvers
import io.github.darvld.wireframe.transport.Transport

/**
 * Configuration for a GraphQL endpoint. Use this class to connect a GraphQL [schema][sdl]
 * to your [resolvers], define [context plugins][contextPlugins], and set the [transport]
 * used for serialization.
 */
@OptIn(WireframeInternal::class)
public class GraphQLConfig internal constructor() {
    private var _resolvers: (Resolvers.() -> Unit)? = null
    private var _contextPlugins: List<KtorContextPlugin>? = null
    private var _sdl: String = ""
    private var _transport: Transport? = null

    // ---Internal getters---

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

    internal val transport: Transport
        get() = _transport ?: throw IllegalStateException("Transport must be specified.")

    // ---Public API---

    /**
     * Provide a GraphQL schema to be used in this endpoint.
     *
     * The schema may be loaded from a single file, or stitched from multiple
     * sources. It must not be empty.
     */
    public fun sdl(schema: String) {
        _sdl = schema
    }

    /**
     * Set the resolvers used to wire the provided GraphQL [schema][sdl].
     *
     * Notice that Wireframe does not check whether the resolvers actually match
     * the schema.
     */
    public fun resolvers(block: Resolvers.() -> Unit) {
        _resolvers = block
    }

    /**Provide a list of context plugins to be applied to each request's context.*/
    public fun contextPlugins(vararg plugins: KtorContextPlugin) {
        _contextPlugins = plugins.toList()
    }

    /**Provide a list of context plugins to be applied to each request's context.*/
    public fun contextPlugins(plugins: Iterable<KtorContextPlugin>) {
        _contextPlugins = plugins.toList()
    }

    /**Sets the [Transport] implementation used to decode requests and encode responses.*/
    public fun transport(transport: Transport) {
        _transport = transport
    }
}
package io.github.darvld.wireframe.routing

import graphql.schema.GraphQLScalarType
import graphql.schema.idl.WiringFactory
import io.github.darvld.wireframe.ResolversDsl
import io.github.darvld.wireframe.WireframeInternal
import io.github.darvld.wireframe.execution.RuntimeWiringFactory
import io.github.darvld.wireframe.execution.SuspendingDataFetcher

/**
 * A starting point for route definitions.

 * In general, this class should not be explicitly instantiated unless
 * you're building a custom framework integration.
 *
 * Resolvers are usually declared as extensions on this class, and
 * framework implementations will provide an instance you can use to
 * apply them.
 */
@JvmInline
@ResolversDsl
@OptIn(WireframeInternal::class)
public value class Resolvers internal constructor(private val map: WiringMap) {
    /**
     * Constructs an empty resolvers map. Use this if you're building
     * a custom integration with a server framework, to serve as
     * starting point for your routing definitions.
     * */
    @WireframeInternal
    public constructor() : this(wiringMap())

    internal val wiringFactory: WiringFactory
        get() = RuntimeWiringFactory(map)

    /**Register a resolver for the field at [path] (e.g. "Account.name").*/
    @WireframeInternal
    public fun <T> resolver(path: String, resolve: suspend ResolverScope.() -> T) {
        map[path] = Wiring.DataFetcherWiring(SuspendingDataFetcher(resolve))
    }

    /**Register a custom scalar type with the given [name].*/
    @WireframeInternal
    public fun scalar(name: String, definition: GraphQLScalarType) {
        map[name] = Wiring.ScalarWiring(definition)
    }
}
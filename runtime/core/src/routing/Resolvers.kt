package io.github.darvld.wireframe.routing

import graphql.schema.idl.WiringFactory
import io.github.darvld.wireframe.execution.RuntimeWiringFactory
import io.github.darvld.wireframe.execution.SuspendingDataFetcher

public fun Resolvers(): Resolvers = Resolvers(mutableMapOf())

@JvmInline
public value class Resolvers internal constructor(
    private val map: MutableMap<String, Wiring>
) {
    internal val wiringFactory: WiringFactory
        get() = RuntimeWiringFactory(map)

    public fun <T> resolver(path: String, resolve: suspend ResolverScope.() -> T) {
        map[path] = Wiring.DataFetcherWiring(SuspendingDataFetcher(resolve))
    }
}
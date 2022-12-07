package io.github.darvld.wireframe.routing

import graphql.GraphQLContext
import graphql.schema.idl.WiringFactory
import io.github.darvld.wireframe.Wiring
import io.github.darvld.wireframe.WiringPath
import io.github.darvld.wireframe.WiringType
import kotlinx.coroutines.CoroutineScope

public abstract class Resolvers {
    private val mapping: MutableMap<WiringPath, Wiring> = mutableMapOf()

    public fun buildWiringFactory(): WiringFactory {
        return RuntimeWiringFactory(mapping)
    }

    protected abstract fun getCoroutineScope(context: GraphQLContext): CoroutineScope

    public fun dataFetcher(typeName: String, fieldName: String, fetcher: SuspendingDataFetcher<Any?>) {
        val path = WiringPath(WiringType.DataFetcher, typeName, fieldName)
        mapping[path] = Wiring.DataFetcherWiring(suspendingDataFetcher(::getCoroutineScope, fetcher))
    }
}
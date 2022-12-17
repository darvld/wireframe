package io.github.darvld.wireframe.execution

import graphql.schema.DataFetcher
import graphql.schema.idl.*
import io.github.darvld.wireframe.WireframeInternal
import io.github.darvld.wireframe.routing.Wiring

@OptIn(WireframeInternal::class)
internal class RuntimeWiringFactory(
    private val mapping: MutableMap<String, Wiring>
) : WiringFactory {
    override fun providesDataFetcher(environment: FieldWiringEnvironment): Boolean {
        return mapping.containsKey(environment.wiringKey)
    }

    override fun getDataFetcher(environment: FieldWiringEnvironment): DataFetcher<*> {
        val wiring = mapping[environment.wiringKey] as Wiring.DataFetcherWiring
        return wiring.fetcher
    }

    private companion object {
        private val FieldWiringEnvironment.wiringKey: String
            get() = "${parentType.name}.${fieldDefinition.name}"
    }
}
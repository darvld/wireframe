package io.github.darvld.wireframe.execution

import graphql.schema.DataFetcher
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.FieldWiringEnvironment
import graphql.schema.idl.ScalarWiringEnvironment
import graphql.schema.idl.WiringFactory
import io.github.darvld.wireframe.WireframeInternal
import io.github.darvld.wireframe.routing.Wiring
import io.github.darvld.wireframe.routing.WiringMap

@OptIn(WireframeInternal::class)
internal class RuntimeWiringFactory(private val wiringMap: WiringMap) : WiringFactory {
    override fun providesDataFetcher(environment: FieldWiringEnvironment): Boolean {
        return wiringMap.containsKey(environment.wiringKey)
    }

    override fun getDataFetcher(environment: FieldWiringEnvironment): DataFetcher<*> {
        val wiring = wiringMap[environment.wiringKey] as Wiring.DataFetcherWiring
        return wiring.fetcher
    }

    override fun providesScalar(environment: ScalarWiringEnvironment): Boolean {
        return wiringMap.containsKey(environment.wiringKey)
    }

    override fun getScalar(environment: ScalarWiringEnvironment): GraphQLScalarType {
        val wiring = wiringMap[environment.wiringKey] as Wiring.ScalarWiring
        return wiring.scalar
    }

    private companion object {
        private val FieldWiringEnvironment.wiringKey: String
            get() = "${parentType.name}.${fieldDefinition.name}"

        private val ScalarWiringEnvironment.wiringKey: String
            get() = scalarTypeDefinition.name
    }
}
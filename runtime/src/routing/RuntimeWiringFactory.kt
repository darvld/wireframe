package io.github.darvld.wireframe.routing

import graphql.schema.DataFetcher
import graphql.schema.idl.*
import io.github.darvld.wireframe.Wiring
import io.github.darvld.wireframe.WiringPath
import io.github.darvld.wireframe.WiringType

@JvmInline
internal value class RuntimeWiringFactory(
    private val mapping: MutableMap<WiringPath, Wiring> = mutableMapOf()
) : WiringFactory {
    private fun FieldWiringEnvironment.wiringKey(): WiringPath = WiringPath(
        wiringType = WiringType.DataFetcher,
        typeName = parentType.name,
        fieldName = fieldDefinition.name
    )

    private fun InterfaceWiringEnvironment.wiringKey(): WiringPath = WiringPath(
        wiringType = WiringType.TypeResolver,
        typeName = interfaceTypeDefinition.name,
    )

    private fun UnionWiringEnvironment.wiringKey(): WiringPath = WiringPath(
        wiringType = WiringType.TypeResolver,
        typeName = unionTypeDefinition.name,
    )

    private fun ScalarWiringEnvironment.wiringKey(): WiringPath = WiringPath(
        wiringType = WiringType.Scalar,
        typeName = scalarTypeDefinition.name,
    )

    override fun providesDataFetcher(environment: FieldWiringEnvironment): Boolean {
        return mapping.containsKey(environment.wiringKey())
    }

    override fun getDataFetcher(environment: FieldWiringEnvironment): DataFetcher<*> {
        return (mapping[environment.wiringKey()] as Wiring.DataFetcherWiring).fetcher
    }

    override fun providesTypeResolver(environment: InterfaceWiringEnvironment): Boolean {
        return mapping.containsKey(environment.wiringKey())
    }

    override fun providesTypeResolver(environment: UnionWiringEnvironment): Boolean {
        return mapping.containsKey(environment.wiringKey())
    }

    override fun providesScalar(environment: ScalarWiringEnvironment): Boolean {
        return mapping.containsKey(environment.wiringKey())
    }
}
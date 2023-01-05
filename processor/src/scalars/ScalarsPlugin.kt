package io.github.darvld.wireframe.scalars

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeAliasSpec
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLScalarType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.WireframeCompilerPlugin

public object ScalarsPlugin : WireframeCompilerPlugin {
    override fun processType(type: GraphQLNamedType, environment: ProcessingEnvironment) {
        // Only process scalars
        if (type !is GraphQLScalarType) return

        val packageName = environment.resolvePackage(type)
        environment.output(packageName, type.name) {
            // Create a type alias for the scalar, use mapped name if available
            val aliasedType = environment.typeMappings[type.name]?.let { ClassName.bestGuess(it) } ?: ANY
            addTypeAlias(TypeAliasSpec.builder(type.name, aliasedType).addKdoc(type.description.orEmpty()).build())

            // Add the provider to allow wiring
            val providerClassName = ClassName(packageName, "${type.name}ScalarProvider")
            addType(buildScalarProvider(type, providerClassName, aliasedType))
        }
    }
}
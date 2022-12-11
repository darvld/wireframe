package io.github.darvld.wireframe.base

import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeAliasSpec
import graphql.schema.*
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.WireframeCompilerPlugin
import io.github.darvld.wireframe.extensions.idTypeAlias
import io.github.darvld.wireframe.extensions.isRouteType

public class BasePlugin : WireframeCompilerPlugin {
    override fun processType(type: GraphQLNamedType, environment: ProcessingEnvironment) {
        // Query, Mutation, and Subscription don't get a generated class
        if (type.isRouteType()) return

        // Don't generate a DTO if there's a custom mapping for this type
        if (environment.typeMappings.containsKey(type.name)) return

        if (type is GraphQLEnumType) {
            processEnumType(type, environment)
            return
        }

        if (type is GraphQLInterfaceType) {
            processInterfaceType(type, environment)
            return
        }

        if (type is GraphQLInputObjectType) {
            processInputType(type, environment)
            return
        }

        if (type is GraphQLObjectType) {
            processOutputType(type, environment)
        }
    }

    override fun beforeProcessing(environment: ProcessingEnvironment) {
        val typeName = environment.idTypeAlias()
        val spec = TypeAliasSpec.builder(typeName.simpleName, STRING).build()

        environment.output(typeName.packageName, typeName.simpleName) { addTypeAlias(spec) }
    }
}

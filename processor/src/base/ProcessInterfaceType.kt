package io.github.darvld.wireframe.base

import com.squareup.kotlinpoet.PropertySpec
import graphql.schema.GraphQLInterfaceType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.extensions.*
import io.github.darvld.wireframe.output

internal fun processInterfaceType(definition: GraphQLInterfaceType, environment: ProcessingEnvironment) {
    val generatedType = environment.resolve(definition)
    val extensionFields = definition.getExtensionFields().toList()

    val spec = buildInterface(generatedType) {
        markAsGenerated()
        addKdoc(definition.description.orEmpty())

        for (field in definition.fields) {
            if (field in extensionFields) continue

            val typeName = environment.typeNameFor(field.type).nullable()
            val property = PropertySpec.builder(field.name, typeName)
                .addKdoc(field.description.orEmpty())
                .build()

            addProperty(property)
        }
    }

    environment.output(generatedType, spec)
}
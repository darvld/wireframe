package io.github.darvld.wireframe.base

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import graphql.schema.GraphQLInputObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.extensions.buildClass
import io.github.darvld.wireframe.extensions.buildConstructor
import io.github.darvld.wireframe.extensions.markAsGenerated
import io.github.darvld.wireframe.extensions.typeNameFor
import io.github.darvld.wireframe.output

internal fun processInputType(definition: GraphQLInputObjectType, environment: ProcessingEnvironment) {
    val generatedType = environment.resolve(definition)

    val spec = buildClass(generatedType) {
        addModifiers(KModifier.DATA)

        markAsGenerated()
        addKdoc(definition.description.orEmpty())

        primaryConstructor(buildConstructor {
            for (field in definition.fields) {
                val fieldTypeName = environment.typeNameFor(field.type)

                val property = PropertySpec.Companion.builder(field.name, fieldTypeName)
                    .addKdoc(field.description.orEmpty())
                    .initializer(field.name)
                    .build()

                addParameter(field.name, fieldTypeName)
                addProperty(property)
            }
        })
    }

    environment.output(generatedType, spec)
}
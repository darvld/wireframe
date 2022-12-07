package io.github.darvld.wireframe.base

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.extensions.*
import io.github.darvld.wireframe.output

internal fun processOutputType(definition: GraphQLObjectType, environment: ProcessingEnvironment) {
    val generatedType = environment.resolve(definition)
    val extensionFields = definition.getExtensionFields().toList()
    val interfaces = definition.interfaces()

    val spec = buildClass(generatedType) {
        addModifiers(KModifier.DATA)

        markAsGenerated()
        addKdoc(definition.description.orEmpty())

        val interfaceFields = mutableListOf<String>()
        for (implemented in interfaces) {
            addSuperinterface(environment.resolve(implemented))
            for (field in implemented.fields) interfaceFields.add(field.name)
        }

        primaryConstructor(buildConstructor {
            for (field in definition.fields) {
                if (field in extensionFields) continue

                // For output DTOs, all fields are nullable, this allows the server to skip non-requested fields
                val typeName = environment.typeNameFor(field.type).nullable()
                val property = PropertySpec.Companion.builder(field.name, typeName)
                    .addKdoc(field.description.orEmpty())
                    .initializer(field.name)

                if (field.name in interfaceFields)
                    property.addModifiers(OVERRIDE)

                addParameter(ParameterSpec.Companion.builder(field.name, typeName).defaultValue("null").build())
                addProperty(property.build())
            }
        })
    }

    environment.output(generatedType, spec)
}
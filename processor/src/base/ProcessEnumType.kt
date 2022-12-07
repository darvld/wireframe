package io.github.darvld.wireframe.base

import com.squareup.kotlinpoet.TypeSpec
import graphql.schema.GraphQLEnumType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.extensions.buildEnum
import io.github.darvld.wireframe.extensions.markAsGenerated
import io.github.darvld.wireframe.output

internal fun processEnumType(definition: GraphQLEnumType, environment: ProcessingEnvironment) {
    val generatedType = environment.resolve(definition)

    val spec = buildEnum(generatedType) {
        markAsGenerated()
        addKdoc(definition.description.orEmpty())

        definition.values.forEach {
            val constant = TypeSpec.anonymousClassBuilder()
                .addKdoc(it.description.orEmpty())
                .build()

            addEnumConstant(it.name, constant)
        }
    }

    environment.output(generatedType, spec)
}
package io.github.darvld.wireframe.scalars

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.schema.GraphQLScalarType
import io.github.darvld.wireframe.extensions.buildClass
import io.github.darvld.wireframe.routing.ScalarProvider

internal fun buildScalarProvider(
    type: GraphQLScalarType,
    className: ClassName,
    aliasedName: TypeName,
): TypeSpec {
    return buildClass(className) {
        addModifiers(KModifier.ABSTRACT)

        superclass(ScalarProvider::class.asTypeName().parameterizedBy(aliasedName, ANY))
        addSuperclassConstructorParameter("name=%S", type.name)
    }
}
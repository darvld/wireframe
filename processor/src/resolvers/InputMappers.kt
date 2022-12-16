package io.github.darvld.wireframe.resolvers

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.schema.*
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.extensions.*

/**Builds a [CodeBlock] to extract a field from a target container (e.g. a map or a GraphQL request), and map them
 * from graphql-java's map representation to the corresponding input DTO or primitive type.*/
internal fun ProcessingEnvironment.buildFieldExtractor(
    unwrap: (TypeName) -> CodeBlock,
    fieldType: GraphQLType,
): CodeBlock {
    val fieldTypeName = typeNameFor(fieldType)

    // Simple types can be extracted using a type cast even if they are wrapped in lists
    if (fieldType.unwrapCompletely() !is GraphQLInputObjectType)
        return unwrap(fieldTypeName)

    val unwrappedFieldTypeName = typeNameFor(fieldType.unwrapCompletely()).nonNullable()
    val sourceTypeName = fieldType.replaceObjectTypes()

    // Lists need to be unwrapped manually when the hierarchy contains a DTO
    if (fieldType.unwrapNonNull() is GraphQLList) return CodeBlock.of(
        format = "(%L)%L",
        unwrap(sourceTypeName),
        buildListUnwrapper(fieldType)
    )

    // Fields with a nullable DTO type need to be wrapped in a `?.let` call
    if (sourceTypeName.isNullable) return CodeBlock.of(
        format = "(%L)?.let·{ %T(it) }",
        unwrap(sourceTypeName),
        unwrappedFieldTypeName
    )

    // Non-nullable DTOs can be extracted using the generated pseudo-constructor
    return CodeBlock.of(
        format = "%T(%L)",
        unwrappedFieldTypeName,
        unwrap(sourceTypeName)
    )
}

/**Map this GraphQL type to a KotlinPoet type name, replacing any DTOs with a `Map<String, Any?>` to allow casting from
 * graphql-java's input arguments.*/
private fun GraphQLType.replaceObjectTypes(): TypeName {
    return when (this) {
        is GraphQLNonNull -> originalWrappedType.replaceObjectTypes().nonNullable()
        is GraphQLList -> LIST.parameterizedBy(originalWrappedType.replaceObjectTypes()).nullable()
        is GraphQLInputObjectType, is GraphQLTypeReference -> MAP.parameterizedBy(STRING, ANY.nullable()).nullable()
        else -> throw IllegalStateException("Unsupported type: $this")
    }
}

/**Unwraps a list containing DTOs at some point in the hierarchy using [List.map].
 *  Multiple nested lists are automatically mapped as well.*/
private fun ProcessingEnvironment.buildListUnwrapper(
    type: GraphQLType,
    receiver: String = "",
): CodeBlock {
    val unwrappedType = type.unwrapNonNull()

    if (unwrappedType is GraphQLList) return buildCodeBlock {
        val unwrapper = buildListUnwrapper(unwrappedType.wrappedType, receiver = "it")

        if (type.isNullable) add("%L?.map·{ %L }", receiver, unwrapper)
        else add("%L.map·{ %L }", receiver, unwrapper)
    }

    return buildCodeBlock {
        val finalType = typeNameFor(type).nonNullable()

        if (type.isNullable) add("%L?.let·{ %T(it) }", receiver, finalType)
        else add("%T(%L)", finalType, receiver)
    }
}
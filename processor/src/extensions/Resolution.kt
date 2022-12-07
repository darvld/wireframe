package io.github.darvld.wireframe.extensions

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.Scalars
import graphql.schema.*
import io.github.darvld.wireframe.ProcessingEnvironment

public val IdAlias: ClassName = ClassName("io.github.darvld.wireframe.mapping", "ID")

public fun ProcessingEnvironment.idTypeAlias(): ClassName {
    return ClassName(basePackage, "ID")
}

public fun generateNameFor(type: GraphQLNamedType): String = when (type) {
    is GraphQLEnumType, is GraphQLInterfaceType -> type.name
    else -> "${type.name}Dto"
}

public fun ProcessingEnvironment.typeNameFor(type: GraphQLType): TypeName {
    return when (type) {
        is GraphQLNonNull -> typeNameFor(type.originalWrappedType).nonNullable()
        is GraphQLList -> listTypeNameFor(type)
        is GraphQLScalarType -> scalarTypeNameFor(type, this)
        is GraphQLNamedType -> resolve(type).nullable()
        else -> throw IllegalArgumentException("Unsupported type: $type.")
    }
}

private fun ProcessingEnvironment.listTypeNameFor(type: GraphQLList): TypeName {
    return LIST.parameterizedBy(typeNameFor(type.originalWrappedType)).nullable()
}

private fun scalarTypeNameFor(type: GraphQLScalarType, environment: ProcessingEnvironment): TypeName = when (type) {
    Scalars.GraphQLBoolean -> BOOLEAN
    Scalars.GraphQLFloat -> FLOAT
    Scalars.GraphQLID -> environment.idTypeAlias()
    Scalars.GraphQLInt -> INT
    Scalars.GraphQLString -> STRING
    else -> throw NotImplementedError("Custom scalar types are not supported yet.")
}.nullable()
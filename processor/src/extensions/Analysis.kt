package io.github.darvld.wireframe.extensions

import graphql.schema.*

public fun GraphQLNamedType.isInternalType(): Boolean {
    return GraphQLTypeUtil.isSystemElement().test(this)
}

public fun GraphQLNamedType.isRouteType(): Boolean {
    if (name == "Query" || name == "Mutation" || name == "Subscription") return true

    return false
}

public val GraphQLType.isNullable: Boolean
    get() = this is GraphQLNullableType

public fun GraphQLType.unwrapNonNull(): GraphQLType {
    return if (this is GraphQLNonNull) originalWrappedType else this
}

public tailrec fun GraphQLType.unwrapCompletely(): GraphQLType {
    return if (this is GraphQLModifiedType) wrappedType.unwrapCompletely() else this
}

public fun GraphQLObjectType.getExtensionFields(): Sequence<GraphQLFieldDefinition> {
    val extensions = extensionDefinitions.flatMap { extension ->
        extension.fieldDefinitions.map { it.name!! }
    }

    return fields.asSequence().filter { it.name in extensions }
}

public fun GraphQLInterfaceType.getExtensionFields(): Sequence<GraphQLFieldDefinition> {
    val extensions = extensionDefinitions.flatMap { extension ->
        extension.fieldDefinitions.map { it.name!! }
    }

    return fields.asSequence().filter { it.name in extensions }
}

@Suppress("UNCHECKED_CAST")
public fun GraphQLImplementingType.interfaces(): Sequence<GraphQLInterfaceType> {
    return interfaces.asSequence().filter { it is GraphQLInterfaceType } as Sequence<GraphQLInterfaceType>
}
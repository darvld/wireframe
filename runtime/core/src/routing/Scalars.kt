package io.github.darvld.wireframe.routing

import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import io.github.darvld.wireframe.WireframeInternal

/**
 * A base class for generated custom scalar providers.
 */
public abstract class ScalarProvider<I, O>(internal val name: String) : Coercing<I, O> {
    public fun getType(): GraphQLScalarType = GraphQLScalarType.newScalar()
        .name(name)
        .coercing(this)
        .build()
}

/**Registers a new [provider] for a custom scalar type.*/
@OptIn(WireframeInternal::class)
public fun <I, O> Resolvers.scalar(provider: ScalarProvider<I, O>) {
    scalar(provider.name, provider.getType())
}
package io.github.darvld.wireframe.execution

import graphql.GraphQLContext

public fun interface ContextPlugin<in T> {
    public suspend fun apply(request: T, context: GraphQLContext)
}
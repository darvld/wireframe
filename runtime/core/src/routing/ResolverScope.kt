package io.github.darvld.wireframe.routing

import graphql.GraphQLContext
import graphql.execution.ExecutionStepInfo
import graphql.schema.DataFetchingEnvironment
import io.github.darvld.wireframe.ResolversDsl
import io.github.darvld.wireframe.WireframeInternal

@JvmInline
@ResolversDsl
public value class ResolverScope @WireframeInternal public constructor(
    public val env: DataFetchingEnvironment
) {
    public inline val context: GraphQLContext
        get() = env.graphQlContext

    public inline val info: ExecutionStepInfo
        get() = env.executionStepInfo
}
package io.github.darvld.wireframe.routing

import graphql.GraphQLContext
import graphql.execution.ExecutionStepInfo
import graphql.schema.DataFetchingEnvironment

public interface ResolverScope {
    public val context: GraphQLContext
    public val info: ExecutionStepInfo
}

@JvmInline
internal value class DefaultResolverScope(private val env: DataFetchingEnvironment) : ResolverScope {
    override val context: GraphQLContext get() = env.graphQlContext
    override val info: ExecutionStepInfo get() = env.executionStepInfo
}
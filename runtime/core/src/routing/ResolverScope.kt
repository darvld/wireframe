package io.github.darvld.wireframe.routing

import graphql.GraphQLContext
import graphql.execution.ExecutionStepInfo
import graphql.schema.DataFetchingEnvironment

public typealias ResolverScope = DataFetchingEnvironment

public inline val ResolverScope.context: GraphQLContext
    get() = graphQlContext

public inline val ResolverScope.info: ExecutionStepInfo
    get() = executionStepInfo

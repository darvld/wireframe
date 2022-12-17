package io.github.darvld.wireframe.routing

import graphql.GraphQLContext
import graphql.execution.ExecutionStepInfo
import graphql.schema.DataFetchingEnvironment
import io.github.darvld.wireframe.ResolversDsl
import io.github.darvld.wireframe.WireframeInternal

/**
 * A context providing access to each request's [context] and execution [info].
 *
 * In a typical GraphQL application, each resolver has access to the `parent`,
 * `args`, `context`, and `info` parameters. Wireframe provides the field's parent
 * and arguments in the resolver's signature, and this class provides access to
 * the remaining fields.
 */
@JvmInline
@ResolversDsl
public value class ResolverScope @WireframeInternal public constructor(
    /**
     * The underlying environment provided to the resolver by graphql-java.
     *
     * This field should not be used manually, it is meant to be used by generated
     * code. Instead, use the [context] and [info] properties, and the arguments
     * provided to the resolver.
     */
    public val env: DataFetchingEnvironment
) {
    /**
     * The [GraphQLContext] for the current request. This context is shared
     * by all resolvers called for this request, so it can be used to propagate
     * information to other resolvers further down in the execution hierarchy.
     */
    public inline val context: GraphQLContext
        get() = env.graphQlContext

    /**
     * The [ExecutionStepInfo] for the current request, providing information
     * about the current step in the resolution process.
     */
    public inline val info: ExecutionStepInfo
        get() = env.executionStepInfo
}
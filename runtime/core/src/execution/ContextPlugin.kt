package io.github.darvld.wireframe.execution

import graphql.GraphQLContext

/**
 * A plugin applied to each GraphQL request, in order to modify its context
 * before handing it down to the resolvers.
 *
 * The [apply] method receives a [GraphQLContext], known as the *base context*,
 * and a `request` parameter, allowing access to platform-specific information,
 * such as HTTP headers, etc.
 */
public fun interface ContextPlugin<in T> {
    /**Apply this plugin to a new [context], based on the given [request].*/
    public suspend fun apply(request: T, context: GraphQLContext)
}
package io.github.darvld.wireframe.ktor

import graphql.GraphQLContext
import io.github.darvld.wireframe.routing.Resolvers
import kotlinx.coroutines.CoroutineScope

internal class KtorResolvers : Resolvers() {
    override fun getCoroutineScope(context: GraphQLContext): CoroutineScope {
        return context.scope
    }
}
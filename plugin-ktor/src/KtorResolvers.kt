package io.github.darvld.wireframe.ktor

import graphql.GraphQLContext
import io.github.darvld.wireframe.routing.Resolvers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

internal class KtorResolvers : Resolvers() {
    override fun getCoroutineScope(context: GraphQLContext): CoroutineScope {
        return CoroutineScope(context.scope.coroutineContext + SupervisorJob())
    }
}
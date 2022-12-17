package io.github.darvld.wireframe

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import io.github.darvld.wireframe.execution.ContextPlugin
import io.github.darvld.wireframe.execution.GraphQLExceptionHandler
import io.github.darvld.wireframe.execution.useScope
import io.github.darvld.wireframe.routing.Resolvers
import kotlinx.coroutines.supervisorScope

/**
 * A GraphQL interpreter than can be used to process [requests][ExecutionInput].
 *
 * It uses graphql-java's [GraphQL] engine under the hood, providing support for
 * coroutine-based resolvers, rather than java's [CompletableFuture][java.util.concurrent.CompletableFuture]
 * API.
 *
 * Using a [WireframeServer] also allows you to use [context plugins][ContextPlugin] to create a
 * custom context for every request (e.g. for authorization, request-scoped dependencies, etc.) and
 * pass it down to your resolvers.
 *
 * This class does not handle serialization, if you need to decode a JSON request as provided
 * by a web server, use the [Transport][io.github.darvld.wireframe.transport.Transport] API.
 */
public class WireframeServer<T> internal constructor(
    private val graph: GraphQL,
    private val contextPlugins: List<ContextPlugin<T>>,
) {
    public suspend fun execute(request: T, input: ExecutionInput): ExecutionResult {
        return supervisorScope {
            // Attach scope to the context
            input.graphQLContext.useScope(this)

            // Apply context plugins
            contextPlugins.forEach {
                it.apply(request, input.graphQLContext)
            }

            // Execute the request
            graph.execute(input)
        }
    }
}

/**
 * Creates and configures a [WireframeServer] given a [schema] definition, [resolvers], and
 * an (optional) [ContextPlugin] list.
 */
public fun <T> WireframeServer(
    schema: String,
    resolvers: Resolvers,
    contextPlugins: List<ContextPlugin<T>> = emptyList(),
): WireframeServer<T> {
    require(schema.isNotBlank()) { "Schema should not be empty or blank." }

    // Parse the schema into a TypeDefinitionRegistry
    val registry = SchemaParser().parse(schema)

    // Create the runtime wiring with the provided Resolvers
    val wiring = RuntimeWiring.newRuntimeWiring()
        .wiringFactory(resolvers.wiringFactory)
        .build()

    // Create the executable schema and graph
    val executableSchema = SchemaGenerator().makeExecutableSchema(registry, wiring)
    val graph = GraphQL.newGraphQL(executableSchema)
        .defaultDataFetcherExceptionHandler(GraphQLExceptionHandler)
        .build()

    return WireframeServer(graph, contextPlugins)
}
package io.github.darvld.wireframe

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import io.github.darvld.wireframe.execution.ContextPlugin
import io.github.darvld.wireframe.execution.GraphQLExceptionHandler
import io.github.darvld.wireframe.execution.scope
import io.github.darvld.wireframe.routing.Resolvers
import kotlinx.coroutines.coroutineScope

public class WireframeServer<T> internal constructor(
    private val graph: GraphQL,
    private val contextPlugins: List<ContextPlugin<T>>,
) {
    public suspend fun execute(request: T, input: ExecutionInput): ExecutionResult {
        return coroutineScope {
            // Attach scope to the context
            input.graphQLContext.scope = this

            // Apply context plugins
            contextPlugins.forEach {
                it.apply(request, input.graphQLContext)
            }

            // Execute the request
            graph.execute(input)
        }
    }
}

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
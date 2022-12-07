package io.github.darvld.wireframe.ktor

import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.github.darvld.wireframe.ktor.transport.decodeRequest
import io.github.darvld.wireframe.ktor.transport.encodeResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.JsonElement

public fun Route.graphQL(
    path: String = "/graphql",
    configure: GraphQLConfig.() -> Unit,
) {
    // Create configuration
    val config = GraphQLConfig().apply(configure)

    // Build the graph
    val graph = buildGraph(this, path, config)

    // Register a POST route handler
    post(path) {
        // Decode the GraphQL request in JSON according to the spec
        val inputBuilder = decodeRequest(call.receiveText())
        if (inputBuilder == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        // Complete the request input with the base context and run
        // context plugins passed to the configuration
        val input = inputBuilder.graphQLContext { context ->
            context.put(ContextKeys.coroutineScope, this)
            context.put(ContextKeys.applicationCall, call)

            config.contextBuilder?.invoke(this, context)
        }.build()

        // Execute the request and encode the response
        val result: ExecutionResult = graph.executeAsync(input).await()
        val response: JsonElement = encodeResponse(result)

        call.respond(response)
    }
}

internal fun buildGraph(route: Route, path: String, config: GraphQLConfig): GraphQL {
    if (config.resolvers == null) route.application.environment.log.warn(
        "No resolvers were defined for the graph at $path. All fields will return null." +
            " Make sure you are calling the `resolvers` function in the endpoint configuration."
    )

    if (config.sdl.isBlank()) error(
        "A non-blank SDL was used for endpoint $path. Check your endpoint configuration" +
            "and ensure you are calling `sdl` to define the GraphQL schema."
    )

    val registry: TypeDefinitionRegistry = SchemaParser().parse(config.sdl)
    val wiring: RuntimeWiring = RuntimeWiring.newRuntimeWiring()
        .wiringFactory(KtorResolvers().apply(config.resolvers ?: { /*empty*/ }).buildWiringFactory())
        .build()

    val schema: GraphQLSchema = SchemaGenerator().makeExecutableSchema(registry, wiring)
    return GraphQL.newGraphQL(schema).build()
}
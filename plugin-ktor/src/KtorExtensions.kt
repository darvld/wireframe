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
import io.github.darvld.wireframe.routing.Resolvers
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.JsonElement

public fun Route.graphQL(
    path: String = "/graphql",
    sdl: String,
    resolvers: Resolvers.() -> Unit,
) {
    // Build the graph
    val graph = buildGraph(sdl, resolvers)

    // Register a POST route handler
    post(path) {
        // Decode the GraphQL request in JSON according to the spec
        val inputBuilder = decodeRequest(call.receiveText())
        if (inputBuilder == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        // Complete the request input with the base context
        val input = inputBuilder.graphQLContext { context ->
            context.put(ContextKeys.coroutineScope, this)
            context.put(ContextKeys.applicationCall, call)
        }.build()

        // Execute the request and encode the response
        val result: ExecutionResult = graph.executeAsync(input).await()
        val response: JsonElement = encodeResponse(result)

        call.respond(response)
    }
}

private fun buildGraph(sdl: String, resolvers: Resolvers.() -> Unit): GraphQL {
    val registry: TypeDefinitionRegistry = SchemaParser().parse(sdl)
    val wiring: RuntimeWiring = RuntimeWiring.newRuntimeWiring()
        .wiringFactory(KtorResolvers().apply(resolvers).buildWiringFactory())
        .build()

    val schema: GraphQLSchema = SchemaGenerator().makeExecutableSchema(registry, wiring)

    return GraphQL.newGraphQL(schema).build()
}
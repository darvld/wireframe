package io.github.darvld.wireframe.ktor

import graphql.ExecutionResult
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

        // Complete the request input with the base context
        val input = inputBuilder.graphQLContext { context ->
            context.put(ContextKeys.coroutineScope, this)
            context.put(ContextKeys.applicationCall, call)
        }.build()

        // Apply context plugins
        config.contextPlugins?.forEach {
            with(it) { updateContext(input.graphQLContext) }
        }

        // Execute the request and encode the response
        val result: ExecutionResult = graph.executeAsync(input).await()
        val response: JsonElement = encodeResponse(result)

        call.respond(response)
    }
}
package io.github.darvld.wireframe.ktor

import graphql.ExecutionResult
import io.github.darvld.wireframe.WireframeServer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Builds a GraphQL endpoint at the given [path]. Use the [configure]
 * block to define the schema and resolvers used for this endpoint.
 *
 * Multiple GraphQL routes could be installed on a single server
 * without any major performance concerns.
 *
 * This route will accept POST calls and treat them as JSON-encoded
 * GraphQL requests. A failed decoding attempt will result in a 400
 * error code.
 */
public fun Route.graphQL(
    path: String = "/graphql",
    configure: GraphQLConfig.() -> Unit,
) {
    // Create configuration
    val config = GraphQLConfig().apply(configure)
    val transport = config.transport
    val server = WireframeServer(
        config.sdl,
        config.resolvers,
        config.contextPlugins
    )

    // Register a POST route handler
    post(path) {
        // Decode the GraphQL request in JSON according to the spec
        val input = transport.decodeRequest(call.receiveText())
        if (input == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        // Add ktor-specific context
        input.graphQLContext.useCall(call)

        // Execute the request
        val result: ExecutionResult = server.execute(this, input)

        // Encode the response and send it
        call.respond(transport.encodeResponse(result))
    }
}
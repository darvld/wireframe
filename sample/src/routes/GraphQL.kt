package io.github.darvld.wireframe.sample.routes

import io.github.darvld.wireframe.ktor.graphQL
import io.github.darvld.wireframe.sample.routes.resolvers.artistResolvers
import io.ktor.server.routing.*
import java.io.File

fun Route.api() = graphQL {
    // Set the GraphQL schema definition
    sdl(loadSchema())

    // We can create our own context for the call based on the original HTTP request
    buildContext { context ->
        // Now "role" will be available to all resolvers
        // You can use this step to decode authorization headers, etc.
        context.put("role", "User")
    }

    // Like Ktor routes, we can define our resolvers as extensions and extract
    // them to a separate file, they will be merged to create the wiring
    resolvers {
        artistResolvers()
    }
}

private fun loadSchema(): String {
    // Load the GraphQL schema from a static file packaged with our server
    return File("graphql/schema.graphql").readText()
}
package io.github.darvld.wireframe.sample.routes

import io.github.darvld.wireframe.ktor.graphQL
import io.github.darvld.wireframe.sample.routes.resolvers.artistResolvers
import io.ktor.server.routing.*
import java.io.File

fun Route.api() = graphQL(sdl = loadSchema()) {
    // Just like Ktor routes, we can define our resolvers as extensions
    // on `Resolvers` and extract them to a separate file, they will be
    // merged automatically to create the runtime wiring
    artistResolvers()
}

private fun loadSchema(): String {
    // Load the GraphQL schema from a static file distributed with our server
    return File("graphql/schema.graphql").readText()
}
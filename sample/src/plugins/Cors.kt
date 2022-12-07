package io.github.darvld.wireframe.sample.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCors() = install(CORS) {
    // Allow JSON requests, this is required for GraphQL requests
    // to be accepted by the server
    allowNonSimpleContentTypes = true

    // Enable compatibility with Apollo Studio and GraphiQL.
    // Production servers should remove this line
    anyHost()
}
package io.github.darvld.wireframe.sample.plugins

import io.github.darvld.wireframe.sample.routes.api
import io.github.darvld.wireframe.sample.routes.sandbox
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() = routing {
    // Serve the API at /graphql
    api()

    // Use the GraphiQL playground as our index page
    sandbox()
}
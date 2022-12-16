package io.github.darvld.wireframe.sample

import io.github.darvld.wireframe.sample.plugins.configureCors
import io.github.darvld.wireframe.sample.plugins.configureRouting
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

// Entry point for Ktor
@Suppress("unused")
fun Application.module() {
    // CORS is required for Apollo Studio interoperability
    configureCors()

    // Add routes for GraphQL and the embedded sandbox
    // Explore the API at http://localhost:4000/
    configureRouting()
}
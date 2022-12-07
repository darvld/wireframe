package io.github.darvld.wireframe.sample.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    // Allow sending and receiving JSON data
    install(ContentNegotiation) { json() }
}
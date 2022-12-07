package io.github.darvld.wireframe.sample.routes

import io.github.darvld.wireframe.sample.plugins.log
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.sandbox() = get("/") {
    log.info("Serving GraphiQL sandbox")

    // Serve a static page with the GraphiQL explorer
    call.respondText(
        contentType = ContentType.Text.Html,
        text = application.environment.classLoader.getResource("index.html")!!.readText()
    )
}
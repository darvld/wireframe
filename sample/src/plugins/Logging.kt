package io.github.darvld.wireframe.sample.plugins

import io.github.darvld.wireframe.ktor.call
import io.github.darvld.wireframe.routing.ResolverScope
import io.github.darvld.wireframe.routing.context
import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import org.slf4j.Logger

/**A shortcut to access the relevant logger for the current pipeline.*/
inline val PipelineContext<Unit, ApplicationCall>.log: Logger
    get() = application.environment.log

/**A shortcut to access the relevant logger for this call.*/
inline val ResolverScope.log: Logger
    get() = context.call.application.environment.log

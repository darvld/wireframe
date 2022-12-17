package io.github.darvld.wireframe.ktor

import io.github.darvld.wireframe.execution.ContextPlugin
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

/**Alias for Ktor's call pipeline context.*/
public typealias CallContext = PipelineContext<Unit, ApplicationCall>

/**Alias for a [ContextPlugin] built on a Ktor application call.*/
public typealias KtorContextPlugin = ContextPlugin<CallContext>
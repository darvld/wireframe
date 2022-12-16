package io.github.darvld.wireframe.transport

import graphql.ExecutionInput
import graphql.ExecutionResult

public interface JsonSerializer {
    public fun decodeRequest(encoded: String): ExecutionInput
    public fun encodeResponse(result: ExecutionResult): String
}
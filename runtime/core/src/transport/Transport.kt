package io.github.darvld.wireframe.transport

import graphql.ExecutionInput
import graphql.ExecutionResult

/**
 * A transport represents a JSON codec that can convert between JSON and GraphQL requests/responses.
 *
 * This interface and its implementations are provided for convenience when implementing support for
 * different server frameworks.
 *
 * Since a [WireframeServer][io.github.darvld.wireframe.WireframeServer] does not handle serialization,
 * we can use a [Transport] to decode the request passed by the HTTP server, and to encode the response
 * before handing it back to the framework.
 */
public interface Transport {
    /**
     * Decodes the provided JSON input as a GraphQL request and returns an [ExecutionInput], or `null`
     * if it does not represent a valid request.
     */
    public fun decodeRequest(encoded: String): ExecutionInput?

    /**Encodes the GraphQL response as a JSON string according to the specification.*/
    public fun encodeResponse(result: ExecutionResult): String
}
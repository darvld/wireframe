package io.github.darvld.wireframe.ktor.transport

import graphql.ExecutionResult
import kotlinx.serialization.json.*

internal fun encodeResponse(result: ExecutionResult): JsonElement {
    return buildJsonObject {
        if (result.errors != null && result.errors.isNotEmpty()) {
            val encodedErrors = result.errors.map { encodeMap(it.toSpecification()) }
            put("errors", JsonArray(encodedErrors))
        }

        if (result.isDataPresent) {
            put("data", result.getData<Any?>().encode())
        }

        if (result.extensions != null) {
            put("extensions", encodeMap(result.extensions))
        }
    }
}

private fun Any?.encode(): JsonElement {
    return when (this) {
        null -> JsonNull
        is String -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is Map<*, *> -> encodeMap(this)
        is Iterable<*> -> encodeIterable(this)
        else -> throw IllegalArgumentException("Could not serialize response element: $this")
    }
}

private fun encodeIterable(iterable: Iterable<*>): JsonElement {
    return JsonArray(iterable.map(Any?::encode))
}

private fun encodeMap(map: Map<*, *>): JsonElement {
    return buildJsonObject {
        map.forEach { (key, value) ->
            require(key is String)
            put(key, value.encode())
        }
    }
}

package io.github.darvld.wireframe.transport

import graphql.ExecutionInput
import graphql.ExecutionResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

/**A JSON [Transport] using the official Kotlin serialization library.*/
public object KotlinxTransport : Transport {
    override fun decodeRequest(encoded: String): ExecutionInput? = runCatching {
        val json = Json.parseToJsonElement(encoded).jsonObject

        val query = json["query"]?.jsonPrimitive?.content
        val operationName = json["operationName"]?.jsonPrimitive?.contentOrNull
        val variables = json["variables"]?.jsonObject?.let(::decodeMap)

        val builder = ExecutionInput.newExecutionInput(query)

        operationName?.let(builder::operationName)
        variables?.let(builder::variables)

        builder.build()
    }.getOrNull()


    override fun encodeResponse(result: ExecutionResult): String {
        val json = buildJsonObject {
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

        return Json.encodeToString(json)
    }
}

private fun decodeAny(element: JsonElement): Any? {
    return when (element) {
        is JsonNull -> null
        is JsonPrimitive -> decodePrimitive(element)
        is JsonArray -> decodeArray(element)
        is JsonObject -> decodeMap(element)
        else -> throw IllegalArgumentException("Could not deserialize $element")
    }
}

private fun decodeMap(element: JsonObject): Map<String, Any?> {
    return element.mapValues { (_, value) -> decodeAny(value) }
}

private fun decodePrimitive(element: JsonPrimitive): Any? {
    return element.contentOrNull
        ?: element.booleanOrNull
        ?: element.intOrNull
        ?: element.longOrNull
        ?: element.floatOrNull
        ?: element.doubleOrNull
}

private fun decodeArray(element: JsonArray): Any {
    return element.map(::decodeAny)
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

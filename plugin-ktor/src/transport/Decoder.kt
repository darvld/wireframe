package io.github.darvld.wireframe.ktor.transport

import graphql.ExecutionInput
import kotlinx.serialization.json.*

internal fun decodeRequest(request: String): ExecutionInput.Builder? {
    val json = Json.parseToJsonElement(request) as? JsonObject ?: return null

    val query = (json["query"] as? JsonPrimitive)?.content ?: return null

    val operationName = (json["operationName"] as? JsonPrimitive)?.contentOrNull
    val variables = (json["variables"] as? JsonObject)?.let(::decodeMap)

    val builder = ExecutionInput.newExecutionInput(query)

    operationName?.let(builder::operationName)
    variables?.let(builder::variables)

    return builder
}

private fun decodeAny(element: JsonElement): Any? {
    return when (element) {
        is JsonNull -> null
        is JsonPrimitive -> decodePrimitive(element)
        is JsonArray -> decodeArray(element)
        is JsonObject -> decodeMap(element)
        else -> throw java.lang.IllegalArgumentException("Could not deserialize $element")
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

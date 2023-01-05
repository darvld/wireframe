package io.github.darvld.wireframe.routing

/**An intermediate map defining the runtime wiring of a GraphQL endpoint.*/
internal typealias WiringMap = MutableMap<String, Wiring>

/**Creates a new empty wiring map.*/
@Suppress("nothing_to_inline")
internal inline fun wiringMap(): WiringMap = mutableMapOf()
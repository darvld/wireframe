package io.github.darvld.wireframe

@JvmInline
internal value class WiringPath(val rawPath: String) {
    constructor(
        wiringType: WiringType,
        typeName: String,
        fieldName: String? = null,
    ) : this("${wiringType.prefix}:$typeName${fieldName?.let { "/$it" } ?: ""}")

    constructor(wiringType: WiringType, typeName: String) : this("${wiringType.prefix}:$typeName")

    inline val wiringType: WiringType get() = WiringType.valueOf(rawPath.substringBefore(':'))

    inline val typeName: String get() = rawPath.substringAfter(':').substringBefore('/')
    inline val fieldName: String get() = rawPath.substringAfter('/')
}
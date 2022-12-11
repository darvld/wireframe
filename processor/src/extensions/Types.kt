@file:Suppress("NOTHING_TO_INLINE")

package io.github.darvld.wireframe.extensions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.Generated

public val GENERATED: ClassName = Generated::class.asClassName()

public inline fun TypeName.nullable(): TypeName {
    return if (isNullable) this else copy(nullable = true)
}

public inline fun TypeName.nonNullable(): TypeName {
    return if (isNullable) copy(nullable = false) else this
}

public fun String.subpackage(subpackage: String): String = when {
    isEmpty() -> subpackage
    subpackage.isEmpty() -> this
    else -> "$this.$subpackage"
}
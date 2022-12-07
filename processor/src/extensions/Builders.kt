@file:Suppress("NOTHING_TO_INLINE")

package io.github.darvld.wireframe.extensions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

public inline fun buildEnum(className: ClassName, builder: TypeSpec.Builder.() -> Unit): TypeSpec {
    return TypeSpec.enumBuilder(className).apply(builder).build()
}

public inline fun buildClass(className: ClassName, builder: TypeSpec.Builder.() -> Unit): TypeSpec {
    return TypeSpec.classBuilder(className).apply(builder).build()
}

public inline fun buildInterface(className: ClassName, builder: TypeSpec.Builder.() -> Unit): TypeSpec {
    return TypeSpec.interfaceBuilder(className).apply(builder).build()
}

public inline fun buildFunction(name: String, builder: FunSpec.Builder.() -> Unit): FunSpec {
    return FunSpec.builder(name).apply(builder).build()
}

public inline fun buildConstructor(builder: FunSpec.Builder.() -> Unit): FunSpec {
    return FunSpec.constructorBuilder().apply(builder).build()
}

public inline fun FunSpec.Builder.addCode(builder: CodeBlock.Builder.() -> Unit) {
    addCode(CodeBlock.Builder().apply(builder).build())
}

public inline fun TypeSpec.Builder.markAsGenerated() {
    addAnnotation(GENERATED)
}

public inline fun FunSpec.Builder.markAsGenerated() {
    addAnnotation(GENERATED)
}
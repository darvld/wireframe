package io.github.darvld.wireframe

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.schema.GraphQLNamedType
import io.github.darvld.wireframe.WireframeCompiler.Output
import io.github.darvld.wireframe.extensions.generateNameFor

/**Provides information about the environment in which the code will be generated.*/
public class ProcessingEnvironment internal constructor(
    public val basePackage: String,
) {
    @JvmInline
    private value class OutputKey(@Suppress("unused") val qualifiedName: String) {
        constructor(packageName: String, fileName: String) : this("$packageName.${fileName}")
    }

    private val packageNameCache: MutableMap<String, String> = mutableMapOf()
    private val nameResolutionCache: MutableMap<GraphQLNamedType, ClassName> = mutableMapOf()

    private val outputs: MutableMap<OutputKey, FileSpec.Builder> = mutableMapOf()

    public fun registerPackageFor(typeName: String, packageName: String) {
        packageNameCache[typeName] = packageName
    }

    public fun resolve(type: GraphQLNamedType): ClassName = nameResolutionCache.getOrPut(type) {
        ClassName(packageNameCache.getOrDefault(type.name, basePackage), generateNameFor(type))
    }

    public fun output(packageName: String, fileName: String, block: FileSpec.Builder.() -> Unit) {
        outputs.getOrPut(OutputKey(packageName, fileName)) { FileSpec.builder(packageName, fileName) }.apply(block)
    }

    public fun getOutputs(): Sequence<Output> {
        return outputs.values.asSequence().map { Output(it.build()) }
    }
}

public fun ProcessingEnvironment.output(className: ClassName, spec: TypeSpec) {
    output(className.packageName, className.simpleName, spec)
}

public fun ProcessingEnvironment.output(packageName: String, fileName: String, spec: TypeSpec) {
    output(packageName, fileName) { addType(spec) }
}

public fun ProcessingEnvironment.output(packageName: String, fileName: String, spec: FunSpec) {
    output(packageName, fileName) { addFunction(spec) }
}
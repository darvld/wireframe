package io.github.darvld.wireframe

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLScalarType
import io.github.darvld.wireframe.WireframeCompiler.Output
import io.github.darvld.wireframe.extensions.generateNameFor
import io.github.darvld.wireframe.extensions.isRouteType
import io.github.darvld.wireframe.extensions.subpackage

/**Provides information about the environment in which the code will be generated.*/
public class ProcessingEnvironment internal constructor(
    public val basePackage: String,
    public val typeMappings: Map<String, String>,
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

    public fun resolvePackage(type: GraphQLNamedType): String = packageNameCache.getOrPut(type.name) {
        // Use mapped types if available
        typeMappings[type.name]?.takeUnless { type is GraphQLScalarType }?.let { mappedName ->
            if (type !is GraphQLOutputType)
                error("Type mapping is only allowed for output types")

            if (type.isRouteType())
                error("Mapping built-in types (Query, Mutation, Subscription) is not allowed")

            return basePackage.subpackage(mappedName.substringBefore("."))
        }

        return basePackage
    }

    public fun resolve(type: GraphQLNamedType): ClassName = nameResolutionCache.getOrPut(type) {
        // Use mapped types when available, but avoid it for custom scalars (use generated alias instead)
        typeMappings[type.name]?.takeUnless { type is GraphQLScalarType }?.let { mappedName ->
            if (type !is GraphQLOutputType)
                error("Type mapping is only allowed for output types")

            if (type.isRouteType())
                error("Mapping built-in types (Query, Mutation, Subscription) is not allowed")

            // Mapped names may begin with a subpackage relative to the project's base package
            return ClassName(
                packageName = basePackage.subpackage(mappedName.substringBeforeLast('.')),
                mappedName.substringAfterLast('.'),
            )
        }

        // Use generated type names when no mapped types are specified
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
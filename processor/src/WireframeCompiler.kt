package io.github.darvld.wireframe

import com.squareup.kotlinpoet.FileSpec
import graphql.language.TypeDefinition
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.github.darvld.wireframe.base.BasePlugin
import io.github.darvld.wireframe.extensions.isInternalType
import io.github.darvld.wireframe.extensions.subpackage
import io.github.darvld.wireframe.resolvers.ResolversPlugin
import java.nio.file.Path

/**Analyzes `.graphqls` schema definitions using graphql-java and outputs type-safe Kotlin code for the types and
 *  operations in the schema.*/
public class WireframeCompiler {
    /**Encapsulates an output element from the code generator. Use it to write the generated code to an [Appendable],
     * or as a file in a target directory.*/
    @JvmInline
    public value class Output(private val spec: FileSpec) {
        public val name: String
            get() = spec.name

        public val packageName: String
            get() = spec.packageName

        public fun writeTo(directory: Path) {
            spec.writeTo(directory)
        }

        public fun writeTo(out: Appendable) {
            spec.writeTo(out)
        }
    }

    /**Represents a piece of input data to be processed by the compiler.*/
    public data class Source(
        val sdl: String,
        val packageName: String? = null,
        val fileName: String? = null,
    )

    public fun process(
        project: String,
        basePackage: String,
        sources: Iterable<Source>,
        plugins: Iterable<WireframeCompilerPlugin> = emptyList(),
    ): Sequence<Output> {
        val parser = SchemaParser()
        val environment = ProcessingEnvironment(project, basePackage)
        val allPlugins = listOf(BasePlugin(), ResolversPlugin()) + plugins

        // Parse all sources and merge declarations into a single registry
        val registry: TypeDefinitionRegistry = sources.fold(TypeDefinitionRegistry()) { current, next ->
            current.merge(environment.processSource(next, parser))
        }

        // Create a schema with an empty wiring, this allows us to analyze the schema generated by graphql-java
        val schema: GraphQLSchema = SchemaGenerator().makeExecutableSchema(
            /* typeRegistry = */ registry,
            /* wiring = */ RuntimeWiring.MOCKED_WIRING
        )

        for (plugin in allPlugins) plugin.beforeProcessing(environment)
        for (element in schema.allTypesAsList.asSequence()) {
            if (element.isInternalType()) continue

            allPlugins.forEach { it.processType(element, environment) }
        }
        for (plugin in allPlugins) plugin.afterProcessing(environment)

        return environment.getOutputs()
    }

    private fun ProcessingEnvironment.processSource(
        source: Source,
        parser: SchemaParser = SchemaParser(),
    ): TypeDefinitionRegistry {
        val registry = parser.parse(source.sdl)

        source.packageName?.let { sourcePackage ->
            for (type: TypeDefinition<*> in registry.types().values) registerPackageFor(
                typeName = type.name,
                packageName = basePackage.subpackage(sourcePackage)
            )
        }

        return registry
    }
}
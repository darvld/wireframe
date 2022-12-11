package io.github.darvld.wireframe

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

abstract class WiringExtension {
    /**Sets the base package for all generated sources.*/
    abstract val packageName: Property<String>

    /**Whether to add the SDL source's filename as a final package segment. Defaults to `true`.*/
    abstract val useFilenamePackage: Property<Boolean>

    /**
     * Sets a custom mapping for the generated sources: instead of generating an output type, you
     * can specify the qualified name, relative to the base package, of a type you want to use as
     * mapping for the GraphQL type.
     */
    abstract val mappedTypes: MapProperty<String, String>

    /**Sets the root directory where GraphQL sources will be located. The search is performed recursively.*/
    abstract val sourcesRoot: DirectoryProperty

    /**Sets the output directory for the generated sources.*/
    abstract val outputDirectory: DirectoryProperty

    companion object {
        const val ProjectExtensionName = "wiring"
    }
}
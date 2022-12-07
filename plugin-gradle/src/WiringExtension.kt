package io.github.darvld.wireframe

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

abstract class WiringExtension {
    abstract val projectName: Property<String>
    abstract val packageName: Property<String>
    abstract val sourcesRoot: DirectoryProperty
    abstract val outputDirectory: DirectoryProperty

    companion object {
        const val ProjectExtensionName = "wiring"
    }
}
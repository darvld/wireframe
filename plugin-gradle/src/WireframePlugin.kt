package io.github.darvld.wireframe

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

@Suppress("unused")
class WireframePlugin : Plugin<Project> {
    private companion object {
        const val TaskGroup = "wiring"
    }

    override fun apply(target: Project) {
        configureExtension(target)
        configureWiringTask(target)
    }

    private fun configureExtension(project: Project) {
        val wiring = project.extensions.create(WiringExtension.ProjectExtensionName, WiringExtension::class.java)

        wiring.packageName.set(project.group.toString())
    }

    private fun configureWiringTask(project: Project) {
        val configuration = project.configurations.create("wireframe") {
            it.isVisible = false
            it.isCanBeConsumed = false
            it.isCanBeResolved = true
        }

        val wiring = project.extensions.getByName(WiringExtension.ProjectExtensionName) as WiringExtension

        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.getByName("main") { sourceSet ->
            // Get the generation task name for this source set
            val taskName = sourceSet.getTaskName("generate", "Wiring")

            // Setup output directory
            val outputDir = project.buildDir.resolve("generated/kotlin/${sourceSet.name}")

            // Register the code generation task
            project.tasks.register(
                /* name = */ taskName,
                /* type = */ GenerateWiringTask::class.java
            ) { task ->
                task.group = TaskGroup
                task.description = "Generate kotlin sources for GraphQL definitions."

                task.packageName.set(wiring.packageName.orElse(project.group.toString()))
                task.useFilenamePackage.set(wiring.useFilenamePackage.orElse(true))
                
                task.pluginJars.setFrom(configuration)

                val sources = wiring.sourcesRoot.orNull
                if (sources != null) {
                    task.sourcesRoot.set(sources)
                } else {
                    val sourceSetRoot = sourceSet.allSource.sourceDirectories.lastOrNull()
                        ?.parentFile
                        ?.resolve("graphql")
                        ?: error("Could not find a source directory for the GraphQL schema")

                    task.sourcesRoot.set(sourceSetRoot)
                }

                val output = wiring.outputDirectory.orNull
                if (output != null) {
                    task.outputDir.set(output)
                } else {
                    task.outputDir.set(outputDir)
                }
            }

            // Configure task to run on every build
            project.tasks.withType(KotlinCompile::class.java) {
                it.dependsOn(taskName)
            }

            // Add generated sources to the corresponding kotlin source set
            project.kotlinExtension.sourceSets.getByName(sourceSet.name).kotlin.srcDir(outputDir)
        }
    }
}
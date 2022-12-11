package io.github.darvld.wireframe

import com.facebook.ktfmt.format.Formatter
import com.facebook.ktfmt.format.FormattingOptions
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.net.URLClassLoader
import java.util.*
import kotlin.io.path.pathString

abstract class GenerateWiringTask : DefaultTask() {
    private companion object {
        val extensionRegex = Regex("graphqls?")
    }

    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val sourcesRoot: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val useFilenamePackage: Property<Boolean>

    @get:Input
    abstract val mappedTypes: MapProperty<String, String>

    @get:InputFiles
    abstract val pluginJars: ConfigurableFileCollection

    @TaskAction
    fun generate(inputChanges: InputChanges) {
        if (
            inputChanges.isIncremental &&
            !inputChanges.getFileChanges(sourcesRoot).any() &&
            !inputChanges.getFileChanges(pluginJars).any()
        ) return

        val pluginLoader = URLClassLoader(
            /* urls = */ pluginJars.files.map { it.toURI().toURL() }.toTypedArray(),
            /* parent = */ WireframeCompilerPlugin::class.java.classLoader
        )

        val plugins = ServiceLoader.load(WireframeCompilerPlugin::class.java, pluginLoader)

        val basePackage = packageName.get()
        val appendFilename = useFilenamePackage.get()
        val rootPath = sourcesRoot.get().asFile.toPath()
        val typeMappings = mappedTypes.get()

        val sources = sourcesRoot.asFileTree.filter { extensionRegex.matches(it.extension) }.map {
            val sourcePackageName = rootPath.relativize(it.toPath()).pathString
                // Use standard path separators
                .replace(Regex("""[/\\]"""), "/")
                // Remove extension
                // If requested, don't include the filename as package
                .substringBeforeLast(if (appendFilename) "." else "/")
                // Replace path separators with package separators
                .replace("/", ".")
                // Remove base package prefix
                .removePrefix(basePackage)

            WireframeCompiler.Source(
                sdl = it.readText(),
                fileName = it.name,
                packageName = sourcePackageName
            )
        }

        val generator = WireframeCompiler()
        val outputPath = outputDir.asFile.get().toPath()

        generator.process(
            sources,
            options = WireframeCompiler.Options(
                basePackage,
                plugins,
                typeMappings,
            )
        ).forEach {
            it.writeTo(outputPath)
        }

        val options = FormattingOptions(FormattingOptions.Style.DROPBOX)
        outputPath.toFile().walk().asSequence().filter { it.isFile }.forEach {
            val code = Formatter.format(options, it.readText())
            it.writeText(code)
        }
    }
}
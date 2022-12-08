import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.cli.common.toBooleanLenient
import java.util.*

plugins {
    kotlin("jvm")
    id("io.github.darvld.wireframe") version "0.5.0"

    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

sourceSets {
    main {
        kotlin.srcDir("src")
        resources.srcDir("resources")
    }

    test {
        kotlin.srcDir("test")
    }
}

dependencies {
    implementation(projects.pluginKtor)

    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.cors)

    implementation(libs.logging.logback)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

// Read local.properties file if it exists
val localProperties = rootProject.file("local.properties").takeIf { it.isFile }
    ?.reader()
    ?.use { reader -> Properties().apply { load(reader) } }
    ?: error("File not found")


// Running & distribution
application {
    mainClass.set("io.ktor.server.cio.EngineMain")

    val isProduction = localProperties.getProperty("production").toBooleanLenient() ?: true
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${!isProduction}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
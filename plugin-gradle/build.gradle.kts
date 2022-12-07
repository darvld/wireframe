import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")

    id("java-gradle-plugin")
    id("maven-publish")
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

gradlePlugin {
    plugins.create("Wireframe") {
        id = "io.github.darvld.wireframe"
        displayName = "Wireframe Gradle Plugin"
        description = "Helper plugin for Wireframe, a GraphQL server library for Kotlin."
        implementationClass = "io.github.darvld.wireframe.WireframePlugin"
    }
}

sourceSets {
    main { java.srcDirs("src") }
    test { java.srcDirs("test") }
}

dependencies {
    implementation(projects.processor)

    implementation(libs.kotlin.plugin)
    implementation(libs.formatting)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
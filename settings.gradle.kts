@file:Suppress("UnstableApiUsage")

import org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS

// Basic configuration
rootProject.name = "wireframe"
include(":processor", ":plugin")
include(":runtime", ":runtime:core", ":runtime:ktor")
include(":transport:kotlinx")
include(":sample")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Project-level plugins
pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        google()
    }

    val kotlinVersion: String by settings
    val nexusPluginVersion: String by settings

    plugins {
        kotlin("jvm").version(kotlinVersion)
        id("io.github.gradle-nexus.publish-plugin").version(nexusPluginVersion)
    }
}

// Centralized dependency management
dependencyResolutionManagement {
    repositoriesMode.set(FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}
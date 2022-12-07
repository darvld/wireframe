@file:Suppress("UnstableApiUsage")

import org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS

// Basic configuration
rootProject.name = "artemis"
include(":processor", ":runtime", ":plugin-gradle", ":plugin-ktor", ":sample")

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

    // Published plugin version, used by sample project
    val version: String by settings

    plugins {
        kotlin("jvm").version(kotlinVersion)

        id("io.github.gradle-nexus.publish-plugin").version(nexusPluginVersion)
        id("io.github.darvld.wireframe").version(version)
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
@file:Suppress("UnstableApiUsage", "LocalVariableName")

import org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS

// Basic configuration
rootProject.name = "kotlin-project-template"

// Project-level plugins
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }

    plugins {
        kotlin("jvm") version "1.7.22"
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
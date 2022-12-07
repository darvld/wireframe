import java.util.*

plugins {
    kotlin("jvm") apply false
    id("io.github.gradle-nexus.publish-plugin")
}

// Read local.properties file if it exists
val localProperties = rootProject.file("local.properties").takeIf { it.isFile }
    ?.reader()
    ?.use { reader -> Properties().apply { load(reader) } }
    ?: error("File not found: local.properties")

// Read credentials and signing keys from the environment (for cloud builds),
// or from the `local.properties` file (for local builds).
fun secret(name: String): String {
    System.getenv(name)?.let { return it }

    val key = name.split("_")
        .joinToString("") { it.toLowerCase().capitalize(Locale.ENGLISH) }
        .decapitalize(Locale.ENGLISH)

    return localProperties.getProperty(key)
        ?: throw Error("Property not found: $key")
}

// Configure Nexus publishing settings and credentials
nexusPublishing.repositories.sonatype {
    stagingProfileId.set(secret("SONATYPE_STAGING_PROFILE_ID"))

    username.set(secret("OSSRH_USERNAME"))
    password.set(secret("OSSRH_PASSWORD"))

    nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
    snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
}

// Configure publications in subprojects
subprojects {
    // Don't publish code samples
    if (project.name == "sample") return@subprojects

    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    // Extensions
    val publishing = project.extensions.getByType<PublishingExtension>()
    val signing = extensions.getByType<SigningExtension>()

    // Signing configuration
    signing.apply {
        useInMemoryPgpKeys(
            secret("SIGNING_KEY_ID"),
            secret("SIGNING_KEY").replace("\\n", "\n"),
            secret("SIGNING_PASSWORD")
        )
    }

    // Wait for signing tasks before publishing
    // This is a workaround for https://youtrack.jetbrains.com/issue/KT-46466
    val dependsOnTasks = mutableListOf<String>()
    tasks.withType<AbstractPublishToMaven>().configureEach {
        dependsOnTasks.add(this.name.replace("publish", "sign").replaceAfter("Publication", ""))
        dependsOn(dependsOnTasks)
    }

    // Stub javadoc to comply with MavenCentral requirements
    val javadocJar by tasks.creating(Jar::class) { archiveClassifier.set("javadoc") }

    // Setup publication metadata
    publishing.publications.withType<MavenPublication>().configureEach {
        // Sign this publication and all its artifacts
        signing.sign(this)

        // Included to comply with MavenCentral requirements
        // Re-assigning the classifier avoids duplication of the artifact
        artifact(javadocJar) { classifier = "javadoc" }

        // Dependency metadata
        pom {
            name.set(project.name)
            description.set("A GraphQL server library for Kotlin.")
            url.set("https://github.com/darvld/wireframe")

            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }

            developers {
                developer {
                    id.set("darvld")
                    name.set("Dario Valdespino")
                    email.set("dvaldespino00@gmail.com")
                }
            }

            scm {
                connection.set("scm:git:ssh://github.com/darvld/wireframe.git")
                url.set("https://github.com/darvld/wireframe/tree/main")
            }
        }
    }
}
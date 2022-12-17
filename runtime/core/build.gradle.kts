import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

kotlin {
    explicitApi()
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

publishing {
    publications.create<MavenPublication>("runtime-core") {
        from(components["java"])
        artifactId = "runtime-core"
    }
}

sourceSets {
    main { java.srcDirs("src") }
    test { java.srcDirs("test") }
}

dependencies {
    api(libs.graphql)
    api(libs.kotlinx.coroutines)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
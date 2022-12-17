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
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        artifactId = "processor"
    }
}

sourceSets {
    main { java.srcDirs("src") }
    test { java.srcDirs("test") }
}

dependencies {
    implementation(projects.runtime.core)

    implementation(libs.graphql)
    implementation(libs.kotlinpoet)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
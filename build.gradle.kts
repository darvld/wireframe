plugins {
    kotlin("jvm")
}

sourceSets {
    main { java.srcDirs("src") }
    test { java.srcDirs("test") }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
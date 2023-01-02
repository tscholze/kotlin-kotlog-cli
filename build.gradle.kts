import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// MARK: - Properties -

group = "io.github.tscholze"
version = "1.0.8"

// MARK: - Plugins -

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("org.jetbrains.dokka") version "1.7.20"
    application
}

// MARK: - Repositories -

repositories {
    mavenCentral()
}

// MARK: - Dependencies -

dependencies {
    // Markdown
    implementation("org.commonmark:commonmark:0.20.0")
    implementation("org.commonmark:commonmark-ext-yaml-front-matter:0.20.0")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.20.0")
    implementation("org.commonmark:commonmark-ext-autolink:0.20.0")

    // Html
    implementation("org.jsoup:jsoup:1.15.3")

    // Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // CLI
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("com.lordcodes.turtle:turtle:0.8.0")

    // Test
    testImplementation(kotlin("test"))
}

// MARK: - Application settings -

application {
    mainClass.set("MainKt")
}

// MARK: - Gradle tasks -

tasks.jar {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
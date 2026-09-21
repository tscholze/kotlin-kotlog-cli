import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// MARK: - Properties -

group = "io.github.tscholze"
version = "1.0.10"

// MARK: - Plugins -

plugins {
    kotlin("jvm") version "2.4.20"
    kotlin("plugin.serialization") version "2.4.20"
    id("org.jetbrains.dokka") version "2.2.0"
    application
}

// MARK: - Repositories -

repositories {
    mavenCentral()
}

// MARK: - Dependencies -

dependencies {
    // Markdown
    implementation("org.commonmark:commonmark:0.30.0")
    implementation("org.commonmark:commonmark-ext-yaml-front-matter:0.30.0")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.30.0")
    implementation("org.commonmark:commonmark-ext-autolink:0.30.0")

    // Html
    implementation("org.jsoup:jsoup:1.23.2")

    // Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    // CLI
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
    implementation("com.lordcodes.turtle:turtle:0.10.0")

    // Test
    testImplementation(kotlin("test"))
}

// MARK: - Application settings -

//application {
//    mainClass.set("MainKt")
//}

// MARK: - Gradle tasks -

tasks.jar {
    manifest.attributes["Main-Class"] = "MainKt"
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}
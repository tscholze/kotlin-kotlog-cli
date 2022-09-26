import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    application
}

group = "io.github.tscholze"
version = "1.0.3"

repositories {
    mavenCentral()
}

dependencies {
    // Markdown
    implementation("org.commonmark:commonmark:0.19.0")
    implementation("org.commonmark:commonmark-ext-yaml-front-matter:0.19.0")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.19.0")
    implementation("org.commonmark:commonmark-ext-autolink:0.19.0")

    // Html
    implementation("org.jsoup:jsoup:1.15.3")

    // Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

    // CLI
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("com.lordcodes.turtle:turtle:0.8.0")

    // Test
    testImplementation(kotlin("test"))
}

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

application {
    mainClass.set("MainKt")
}
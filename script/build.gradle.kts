plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "com.yeoljeong"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("ScriptApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Slack
    implementation("com.slack.api:slack-api-client:1.45.0")

    // Add these dependencies if you want to use the Kotlin DSL for building rich messages
    implementation("com.slack.api:slack-api-model-kotlin-extension:1.45.0")
    implementation("com.slack.api:slack-api-client-kotlin-extension:1.45.0")

    implementation("org.kohsuke:github-api:1.325")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
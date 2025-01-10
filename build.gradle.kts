plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("com.epages.restdocs-api-spec") version "0.18.2"
    id ("org.jetbrains.kotlin.jvm") version "1.9.25"
    id ("org.jetbrains.kotlin.plugin.spring") version "1.9.25"
    id ("org.jetbrains.kotlin.plugin.jpa") version "1.9.25"
    id ("org.jetbrains.kotlin.plugin.lombok") version "1.9.25"
    id ("io.freefair.lombok") version "8.10"
}

group = "gc"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    create("asciidoctorExt")
}

repositories {
    mavenCentral()
}

dependencies {
    // SpringBoot
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation ("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation ("org.jetbrains.kotlin:kotlin-reflect")

    // h2
    runtimeOnly("com.h2database:h2")

    // MySQL
    runtimeOnly("com.mysql:mysql-connector-j")

    // RestDocs
    add("asciidoctorExt", "org.springframework.restdocs:spring-restdocs-asciidoctor")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.18.2")

    // Logstash
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // Slack
    implementation("com.slack.api:slack-api-client:1.45.0")

    // Add these dependencies if you want to use the Kotlin DSL for building rich messages
    implementation("com.slack.api:slack-api-model-kotlin-extension:1.45.0")
    implementation("com.slack.api:slack-api-client-kotlin-extension:1.45.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val snippetsDir = file("build/generated-snippets")

tasks.test {
    outputs.dir(snippetsDir)
}

tasks.asciidoctor {
    inputs.dir(snippetsDir)
    configurations("asciidoctorExt")

    sources { // 특정 파일만 html로 만든다.
        include("**/index.adoc")
    }
    baseDirFollowsSourceFile() // 다른 adoc 파일을 include 할 때 경로를 baseDir로 맞춘다.
    dependsOn(tasks.test)
}

tasks.bootJar {
    dependsOn(tasks.asciidoctor)
    from("${tasks.asciidoctor.get().outputDir}") {
        into("static/docs")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity") // Java EE면 javax.persistence.Entity
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}
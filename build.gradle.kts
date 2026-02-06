plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "1.9.23"
    id("io.ktor.plugin") version "3.0.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Основные зависимости Ktor 3.0.0
    implementation("io.ktor:ktor-server-core:3.0.0")
    implementation("io.ktor:ktor-server-netty:3.0.0")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")

    // Для логирования
    implementation("io.ktor:ktor-server-call-logging:3.0.0")
    implementation("io.ktor:ktor-server-status-pages:3.0.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.24.1")
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")
    implementation("org.apache.logging.log4j:log4j-api:2.24.1")

    // Koin для DI
    implementation("io.insert-koin:koin-core:3.5.6")
    implementation("io.insert-koin:koin-ktor:3.5.6")
    implementation("io.insert-koin:koin-logger-slf4j:3.5.6")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(18)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}
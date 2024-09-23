plugins {
    kotlin("jvm") version "1.8.22"
    id("org.jetbrains.compose") version "1.5.0" // Plugin de Compose
}

group = "fer.rom.mu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google() // Repositorio de Google para Jetpack Compose
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // Repositorio de JetBrains para Compose Desktop
}

dependencies {
    implementation(compose.desktop.currentOs) // Jetpack Compose Desktop
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))  // Configura JDK 11
    }
}

compose.desktop {
    application {
        mainClass = "MainKt" // Cambia esto según tu clase principal
    }
}
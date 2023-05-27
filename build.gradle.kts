import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("mysql:mysql-connector-java:8.0.15")
    implementation("com.h2database:h2:1.3.148")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("io.rest-assured:rest-assured:5.3.0")
    testImplementation("com.google.code.gson:gson:2.10.1")
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
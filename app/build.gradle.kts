plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("pl.halun.tools.photo.location.AppKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

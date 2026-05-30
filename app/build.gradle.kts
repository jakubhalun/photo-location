plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.20"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.gradleup.shadow") version "9.0.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjfx:javafx-controls:22.0.2")
    implementation("org.openjfx:javafx-fxml:22.0.2")
    implementation("com.drewnoakes:metadata-extractor:2.19.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("io.mockk:mockk:1.13.12")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("pl.halun.tools.photo.location.AppKt")
    applicationDefaultJvmArgs = listOf("--add-modules", "javafx.web", "javafx.controls")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named("distTar") {
    dependsOn("shadowJar")
}

tasks.named("distZip") {
    dependsOn("shadowJar")
}

tasks.named("startScripts") {
    dependsOn("shadowJar")
}

tasks.named("startShadowScripts") {
    dependsOn("jar")
}

val javafxModules = arrayOf("controls", "fxml", "graphics")

javafx {
    modules = listOf("javafx.controls", "javafx.fxml")
    version = "20.0.2"
}

tasks.shadowJar {
    archiveClassifier.set("")
    mergeServiceFiles()
}

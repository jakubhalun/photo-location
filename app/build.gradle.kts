plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjfx:javafx-controls:20.0.2")
    implementation("org.openjfx:javafx-fxml:20.0.2")
    implementation("com.drewnoakes:metadata-extractor:2.18.0")
    implementation("org.dom4j:dom4j:2.1.4")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("io.mockk:mockk:1.13.7")
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

val javafxModules = arrayOf("controls", "fxml", "graphics")

javafx {
    modules = listOf("javafx.controls", "javafx.fxml")
    version = "20.0.2"
}

tasks.shadowJar {
    archiveClassifier.set("")
    mergeServiceFiles()
}

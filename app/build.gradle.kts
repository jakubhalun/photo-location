plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("org.openjfx.javafxplugin") version "0.0.13"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjfx:javafx-controls:20.0.2")
    implementation("org.openjfx:javafx-fxml:20.0.2")
    implementation("com.drewnoakes:metadata-extractor:2.18.0")

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
    applicationDefaultJvmArgs = listOf("--module-path", "\$APP_HOME/lib", "--add-modules", "javafx.controls,javafx.fxml")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

val javafxModules = arrayOf("controls", "fxml", "graphics")

javafx {
    modules = javafxModules.map { "javafx.$it" }
}

plugins {
    java
}

group = "ru.bmstu"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("io.rest-assured:rest-assured:5.5.0")
    testImplementation("io.rest-assured:json-path:5.5.0")

    testImplementation("io.qameta.allure:allure-junit5:2.29.0")
    testImplementation("io.qameta.allure:allure-rest-assured:2.29.0")

    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")

    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.16")
}

tasks.withType<Test> {
    useJUnitPlatform()

    systemProperty("allure.results.directory", buildDir.resolve("allure-results").absolutePath)
    systemProperty("base.url", System.getProperty("base.url", "http://localhost:8080"))

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

val allureResultsDir = layout.buildDirectory.dir("allure-results")
val allureReportDir = layout.buildDirectory.dir("reports/allure-report")
val isWindows = System.getProperty("os.name").lowercase().contains("win")
val allureExe = if (isWindows) "allure.cmd" else "allure"

tasks.register<Exec>("allureReport") {
    description = "Generate Allure HTML report from test results"
    group = "verification"
    workingDir = layout.buildDirectory.get().asFile
    commandLine(
        allureExe, "generate", "--clean",
        "-o", allureReportDir.get().asFile.absolutePath,
        allureResultsDir.get().asFile.absolutePath
    )
}

tasks.register<Exec>("allureServe") {
    description = "Generate and open Allure report in browser"
    group = "verification"
    workingDir = layout.buildDirectory.get().asFile
    commandLine(allureExe, "serve", allureResultsDir.get().asFile.absolutePath)
}
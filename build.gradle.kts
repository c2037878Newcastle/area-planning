plugins {
    id("java-library")
    id("application")
    id("idea")
}

group = "com.shmarov"
version = "1.0.0"

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.gurobi:gurobi:12.0.1")
    api("com.google.code.gson:gson:2.13.1")
}

application {
    mainClass = "shmarovfedor.RunGUI"
    applicationDefaultJvmArgs = listOf("-Xmx15G", "-Xms10G")
}
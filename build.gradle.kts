plugins {
    id("java-library")
    id("application")
}

group = "com.shmarov"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(23))
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.gurobi:gurobi:12.0.1")
}

application {
    mainClass = "shmarovfedor.areaplanning.graphics.use.RunGUI"
}
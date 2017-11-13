description = "Runner Project - ${project.version}"

plugins {
    application
}

application {
    mainClassName = "me.viniciusarnhold.altaria.Runner"
}

dependencies {
    "compile"(project(":core"))
    "runtimeOnly"(project(":commands"))
    "compile"("commons-cli:commons-cli:${properties["commons_cli_version"]}")
}
description = "Runner Project - ${project.version}"

dependencies {
    "compile"(project(":core"))
    "runtimeOnly"(project(":commands"))
    "compile"("commons-cli:commons-cli:${properties["commons_cli_version"]}")
}
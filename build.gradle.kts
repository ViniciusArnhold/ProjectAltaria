buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(dependencyNotation = "org.junit.platform:junit-platform-gradle-plugin:${properties["junit_plugin_version"]}")
        classpath(dependencyNotation = "org.jetbrains.kotlin:kotlin-gradle-plugin:${properties["kotlin_version"]}")
    }
}

allprojects {
    group = "me.viniciusarnhold.${rootProject.name}"
    version = "0.1"

    repositories {
        mavenCentral()
        jcenter()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

plugins {
    id("org.sonarqube").version("2.4")
    id("build-dashboard")
}

subprojects {

    apply {
        plugin("kotlin")
        plugin("project-report")
        plugin("org.junit.platform.gradle.plugin")
        plugin("jacoco")
    }

    dependencies {
        //Kotlin lib
        "compile"("org.jetbrains.kotlin:kotlin-stdlib-jre8:${properties["kotlin_version"]}")

        //Test
        "testCompile"("org.junit.jupiter:junit-jupiter-api:${properties["junit_version"]}")
        "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:${properties["junit_version"]}")
        "testRuntime"("org.junit.platform:junit-platform-launcher:${properties["junit_platform_version"]}")
        "testCompile"("org.assertj:assertj-core:${properties["assertj_version"]}")
        "testCompile"("org.mockito:mockito-core:${properties["mockito_version"]}")

        //Log4j
        "compile"("org.apache.logging.log4j:log4j-core:${properties["log4j_version"]}")
        "compile"("org.apache.logging.log4j:log4j-api:${properties["log4j_version"]}")
    }

    configure<org.junit.platform.gradle.plugin.JUnitPlatformExtension> {
        filters {
            engines {
                exclude(" junit -vintage")
            }
        }
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "4.1-rc-1"
}
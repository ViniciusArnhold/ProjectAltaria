import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.include
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.junit.platform.gradle.plugin.JUnitPlatformExtension

apply {
    from(file("gradle/scripts/version.gradle.kts"))
}

plugins {
    id("org.sonarqube").version("2.4")
    id("build-dashboard")
    base
    id("org.junit.platform.gradle.plugin").apply(false)
    kotlin("jvm").version("1.1.51")
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

tasks.withType<Wrapper> {
    gradleVersion = "4.6-20180105235841+0000"
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("project-report")
        plugin("org.junit.platform.gradle.plugin")
        plugin("jacoco")
    }

    dependencies {
        //Kotlin lib
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))

        //Test
        testImplementation("org.jetbrains.spek:spek-api:${properties["spek_version"]}")
        testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:${properties["spek_version"]}")

        testCompile("org.junit.jupiter:junit-jupiter-api:${properties["junit_version"]}")
        testCompile("org.junit.jupiter:junit-jupiter-engine:${properties["junit_version"]}")
        testCompile("org.junit.platform:junit-platform-launcher:${properties["junit_platform_version"]}")


        testImplementation("org.assertj:assertj-core:${properties["assertj_version"]}")

        testImplementation("org.mockito:mockito-core:${properties["mockito_version"]}")

        //Log4j
        implementation("org.apache.logging.log4j:log4j-core:${properties["log4j_version"]}")
        implementation("org.apache.logging.log4j:log4j-api:${properties["log4j_version"]}")
    }

    configure<JUnitPlatformExtension> {
        filters {
            engines {
                include("spek")
            }
        }
    }

    tasks.withType(KotlinCompile::class.java) {
        kotlinOptions.jvmTarget = "1.8"
    }
}
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    java
    application
}

group = "by.overpass"
version = "0.2"

application {
    mainClass.set("by.overpass.depstoml.MainKt")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    from(
        configurations.runtimeClasspath
            .get()
            .map {
                if (it.isDirectory) {
                    it
                } else {
                    zipTree(it)
                }
            }
    )
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler.embeddable)
    implementation(libs.clikt)
    testImplementation(libs.kotlin.test)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}
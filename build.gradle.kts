plugins {
    alias(libs.plugins.kotlin.jvm)
}

subprojects {
    group = "org.burufi.codegen"
    version = "0.1-SNAPSHOT"

    apply(plugin = "org.jetbrains.kotlin.jvm")

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict", "-Xjvm-default=all-compatibility")
        }
    }

    repositories {
        mavenCentral()
    }

    tasks.test {
        useJUnitPlatform()
    }
}

repositories {
    mavenCentral()
}

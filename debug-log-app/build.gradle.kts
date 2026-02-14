plugins {
    id("application")
    alias(libs.plugins.google.ksp)
}

dependencies {
    ksp(project(":debug-log-processor"))
    implementation(project(":debug-log-processor"))
}

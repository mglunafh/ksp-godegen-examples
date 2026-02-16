plugins {
    id("application")
    alias(libs.plugins.google.ksp)
}

dependencies {
    ksp(project(":csv-builder-processor"))
    implementation(project(":csv-builder-processor"))
}

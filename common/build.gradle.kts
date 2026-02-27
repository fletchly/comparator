plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.dokka-convention")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization)
    implementation(libs.bundles.ktor.client)
}
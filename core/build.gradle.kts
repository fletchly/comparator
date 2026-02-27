plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.dokka-convention")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
    implementation(libs.kotlinxSerialization)
}
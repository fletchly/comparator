plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.dokka-convention")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(libs.kotlinx.serialization)

    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
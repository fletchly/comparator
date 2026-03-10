/*
 * This file is part of comparator, licensed under the Apache License 2.0
 *
 * Copyright (c) 2026 fletchly <https://github.com/fletchly>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("kotlin-jvm")
    alias(libs.plugins.kotlin.serialization)
//    alias(libs.plugins.ktor)
}

dependencies {
    implementation(project(":comparator-core"))
    implementation(project(":comparator-common"))

    implementation(libs.bundles.ktor.server)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
}

val bunExecutable: String by lazy {
    val fromEnv = System.getenv("BUN_INSTALL")
    if (fromEnv != null) {
        "$fromEnv/bin/bun"
    } else {
        "${System.getProperty("user.home")}/.bun/bin/bun"
    }
}


val frontendBunInstall by tasks.registering(Exec::class) {
    workingDir = file("$projectDir/frontend")
    commandLine(bunExecutable, "install")
    inputs.file("$projectDir/frontend/bun.lock")
    outputs.dir("$projectDir/frontend/node_modules")
}

val frontendBuild by tasks.registering(Exec::class) {
    dependsOn(frontendBunInstall)
    workingDir = file("$projectDir/frontend")
    commandLine(bunExecutable, "run", "build")
    inputs.dir("$projectDir/frontend/src")
    inputs.file("$projectDir/frontend/svelte.config.js")
    outputs.dir("$projectDir/frontend/build")
}

// Copy the SvelteKit build output into Ktor's classpath resources
val copySvelteBuild by tasks.registering(Copy::class) {
    dependsOn(frontendBuild)
    from("$projectDir/frontend/build")
    into("$projectDir/src/main/resources/web")
}

tasks.named("processResources") {
    dependsOn(copySvelteBuild)
}

//tasks.register<Copy>("copySvelteBuild") {
//    from("$projectDir/frontend/build")
//    into("$projectDir/src/main/resources/web")
//    dependsOn(":web:frontendNpmBuild")  // custom task or use npmRunBuild if you configure it
//}
//tasks.named("build") { dependsOn("copySvelteBuild") }
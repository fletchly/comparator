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
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.dokka-convention")
    alias(libs.plugins.paperweightUserdev)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.runPaper)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":common"))

    implementation(libs.kotlinx.coroutines)
    implementation(libs.bundles.configurate)

    paperweight.paperDevBundle(libs.versions.paper)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
}

tasks {
    runServer {
        notCompatibleWithConfigurationCache("Invocation of 'Task.project' at execution time is unsupported with the configuration cache.")
        minecraftVersion(libs.versions.minecraft.get())
    }

    build {
        dependsOn("shadowJar")
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}


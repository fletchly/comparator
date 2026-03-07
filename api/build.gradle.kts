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
    id("dokka")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.maven.publish)
}

version = "1.0.0"

dependencies {
    implementation(kotlin("reflect"))
    implementation(libs.kotlinx.serialization)
    compileOnly(libs.paper.api)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testCompileOnly(libs.paper.api)
}

dokka {
    dokkaSourceSets.main {
        samples.from("src/test/kotlin/io/fletchly/comparator/example")
    }
    dokkaPublications.html {
        moduleName.set("Comparator Tool API")
    }
}

tasks.test {
    exclude("**/example/**")
}

java {
    withSourcesJar()
}

tasks.matching { it.name == "generateMetadataFileForMavenPublication" }.configureEach {
    dependsOn(tasks.matching { it.name == "dokkaJavadocJar" })
}

mavenPublishing {
    publishToMavenCentral()

    coordinates(rootProject.group.toString(), "comparator-api", version.toString())

    pom {
        name.set("Comparator API")
        description.set("Tool API for the Comparator Minecraft plugin")
        url.set("https://github.com/fletchly/comparator")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("fletchly")
                name.set("Owen Mount")
                email.set("fletchly@travisland.net")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/fletchly/comparator.git")
            developerConnection.set("scm:git:ssh://github.com/fletchly/comparator.git")
            url.set("https://github.com/fletchly/comparator")
        }
    }

    signAllPublications()
}
plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.dokka-convention")
    alias(libs.plugins.paperweightUserdev)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.runPaper)
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)
}

tasks {
    runServer {
        minecraftVersion(libs.versions.minecraft.toString())
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


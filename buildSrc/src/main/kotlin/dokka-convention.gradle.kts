package buildsrc.convention

plugins {
    id("org.jetbrains.dokka")
}

dokka {
    dokkaPublications.html {
        moduleName.set(project.name)
        moduleVersion.set(project.version.toString())
        // Standard output directory for HTML documentation
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
        failOnWarning.set(false)
        suppressInheritedMembers.set(false)
        suppressObviousFunctions.set(true)
        offlineMode.set(false)
    }
}
import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne.surf"
version = findProperty("version") as String

surfPaperPluginApi {
    mainClass("dev.slne.vehicle.VehicleTest")
    authors.add("Ammo")
    generateLibraryLoader(false)

    runServer {
        withSurfApiBukkit()
    }
}
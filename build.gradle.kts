import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne.surf"
version = findProperty("version") as String

surfPaperPluginApi {
    mainClass("dev.slne.vehicle.SurfVehicle")
    authors.add("Ammo")
    generateLibraryLoader(false)
    foliaSupported(true)

    runServer {
        withSurfApiBukkit()
    }
}
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    enableTransitiveAccessWideners.set(true)
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

repositories {
    mavenCentral()
}

val shadowCommon = configurations.create("shadowCommon")
dependencies {
    minecraft("com.mojang:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.10")
    modImplementation("net.fabricmc:fabric-loader:0.14.21")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:0.89.3+1.20.1")
    modImplementation(fabricApi.module("fabric-command-api-v2", "0.89.3+1.20.1"))

    implementation(project(":common", configuration = "namedElements"))
    "developmentFabric"(project(":common", configuration = "namedElements"))

    modImplementation("com.cobblemon:fabric:1.4.1+1.20.1")

    shadowCommon(project(":common"))
}

tasks {

    jar {
        archiveBaseName.set("wondertrade-${project.name}")
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        exclude("architectury.common.json", "com/**/*")
        exclude("org/**/*")
        exclude("kotlin/**/*")
        archiveClassifier.set("dev-shadow")
        archiveBaseName.set("CobblemonTools-${project.name}")
        configurations = listOf(shadowCommon)
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        archiveBaseName.set("CobblemonTools-${project.name}")
        archiveVersion.set("${rootProject.version}")
    }

}
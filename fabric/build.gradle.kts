import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    `maven-publish`
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

    modImplementation("com.cobblemon:fabric:1.5.0+1.20.1-SNAPSHOT")

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

publishing {
  repositories {
    maven {
      name = "roanoke-development"
      url = uri("https://vault.roanoke.dev/releases")
      credentials {
        username = property("roanokeDevelopmentUsername").toString()
        password = property("roanokeDevelopmentPassword").toString()
      }
    }
  }
  publications {
    create<MavenPublication>("mavenJava") {
      // Assuming 'java' component exists and represents what you want to publish
        setOf("apiElements", "runtimeElements")
    .flatMap { configName -> configurations[configName].hierarchy }
    .forEach { configuration ->
        configuration.dependencies.removeIf { dependency ->
            dependency.name == "common"
        }
    }
      from(components["java"])

      // Set groupId, artifactId, and version according to your project properties
      groupId = "dev.roanoke"
      artifactId = "CobblemonTools-${project.name}"
      version = rootProject.version.toString()
    }
  }
}

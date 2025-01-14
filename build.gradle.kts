import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.ajoberstar.grgit.Grgit

plugins {
    java
   `java-library`

    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.ajoberstar.grgit") version "5.2.1"

    idea
    eclipse
}

the<JavaPluginExtension>().toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
}

configurations.all {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
}

tasks.compileJava.configure {
    options.release.set(17)
}

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://libraries.minecraft.net/") }
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.9.0")
    implementation("dev.notmyfault.serverlib:ServerLib:2.3.4")
    implementation("io.papermc:paperlib:1.0.8")
}

var buildNumber by extra("")
ext {
    val git: Grgit = Grgit.open {
        dir = File("$rootDir/.git")
    }
    val commit: String? = git.head().abbreviatedId
    buildNumber = if (project.hasProperty("buildnumber")) {
        project.properties["buildnumber"] as String
    } else {
        commit.toString()
    }
}

version = String.format("%s-%s", rootProject.version, buildNumber)

tasks.named<Copy>("processResources") {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set(null as String?)
    dependencies {
        relocate("org.incendo.serverlib", "com.arcaniax.gobrush.serverlib") {
            include(dependency("dev.notmyfault.serverlib:ServerLib:2.3.4"))
        }
        relocate("io.papermc.lib", "com.arcaniax.gobrush.paperlib") {
            include(dependency("io.papermc:paperlib:1.0.8"))
        }
    }
    minimize()
}

tasks.named("build").configure {
    dependsOn("shadowJar")
}

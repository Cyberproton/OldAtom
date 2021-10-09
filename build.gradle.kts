val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
val shadowJar: com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar by tasks
val projectGroup: String by project
val projectVersion: String by project
val artifact: String by project
val exposedVersion: String by project

plugins {
    java
    id("maven-publish")
    kotlin("jvm") version "1.5.30"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "papermc-repo"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven { url = uri("https://jitpack.io") }
    maven {
        url = uri("https://libraries.minecraft.net/")
    }
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    shadow("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.0-SNAPSHOT")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("me.arcaniax:HeadDatabase-API:1.1.0")
    compileOnly("com.mojang:authlib:1.5.21")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    implementation("net.objecthunter:exp4j:0.4.8")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
    javaParameters = true
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = projectGroup
            artifactId = artifact
            version = projectVersion
            artifact(tasks["shadowJar"])
        }
    }
}

shadowJar.archiveBaseName.set(artifact)
shadowJar.archiveClassifier.set("")
shadowJar.archiveVersion.set(projectVersion)
shadowJar.relocate("co.aikar.commands", "me.cyberproton.atom.lib.co.aikar.commands")
shadowJar.relocate("co.aikar.locales", "me.cyberproton.atom.lib.co.aikar.locales")
shadowJar.relocate("net.objecthunter.exp4j", "me.cyberproton.atom.lib.net.objecthunter.exp4j")
shadowJar.dependencies {
    exclude(dependency("com.destroystokyo.paper:paper-api"))
}
shadowJar.exclude("**/*.kotlin_metadata")
shadowJar.exclude("**/*.kotlin_module")
shadowJar.exclude("**/*.kotlin_builtins")
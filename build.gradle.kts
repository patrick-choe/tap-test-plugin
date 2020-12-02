/*
 * Copyright (C) 2020 PatrickKR
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact me on <mailpatrickkr@gmail.com>
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.20"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.github.patrick-mc"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    compileOnly("com.destroystokyo.paper:paper-api:1.16.4-R0.1-SNAPSHOT")

    implementation("com.github.noonmaru:tap:3.2.6")
    implementation("com.github.noonmaru:kommand:0.6.3")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<ShadowJar> {
        archiveClassifier.set("")
    }

    create<Copy>("distJar") {
        from(shadowJar)

        val fileName = "${project.name.split("-").joinToString("") { it.capitalize() }}.jar"

        rename {
            fileName
        }

        if (System.getProperty("os.name").startsWith("Windows")) {
            val pluginsDir = "W:\\Servers\\1.16.4\\plugins"
            val updateDir = "$pluginsDir\\update"

            if (file("$pluginsDir\\$fileName").exists()) {
                into(updateDir)
            } else {
                into(pluginsDir)
            }
        }
    }
}
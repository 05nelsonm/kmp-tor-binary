/*
 * Copyright (c) 2021 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.multiplatform) apply(false)
    alias(libs.plugins.android.library) apply(false)
    alias(libs.plugins.binaryCompat)
    alias(libs.plugins.gradleVersions)
}

allprojects {

    findProperty("GROUP")?.let { group = it }
    findProperty("VERSION_NAME")?.let { version = it }
    findProperty("POM_DESCRIPTION")?.let { description = it.toString() }

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

}

plugins.withType<YarnPlugin> {
    the<YarnRootExtension>().lockFileDirectory = rootDir.resolve(".kotlin-js-store")
}

@Suppress("LocalVariableName")
apiValidation {
    val KMP_TARGETS = findProperty("KMP_TARGETS") as? String
    val CHECK_PUBLICATION = findProperty("CHECK_PUBLICATION") as? String
    val KMP_TARGETS_ALL = System.getProperty("KMP_TARGETS_ALL") != null
    val TARGETS = KMP_TARGETS?.split(',')

    if (CHECK_PUBLICATION != null) {
        ignoredProjects.add("check-publication")
    } else {
        val JVM = TARGETS?.contains("JVM") != false
        val ANDROID = TARGETS?.contains("ANDROID") != false

        // Don't check these projects when building JVM only or Android only
        if (!KMP_TARGETS_ALL && ((!ANDROID && JVM) || (ANDROID && !JVM))) {
            ignoredProjects.add("kmp-tor-binary-android")
            ignoredProjects.add("kmp-tor-binary-extract")
            ignoredProjects.add("kmp-tor-binary-geoip")
        }
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    // Example 1: reject all non stable versions
    rejectVersionIf {
        isNonStable(candidate.version)
    }

    // Example 2: disallow release candidates as upgradable versions from stable versions
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }

    // Example 3: using the full syntax
    resolutionStrategy {
        componentSelection {
            all {
                if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                    reject("Release candidate")
                }
            }
        }
    }
}

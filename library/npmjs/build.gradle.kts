/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
import dev.petuska.npm.publish.extension.domain.NpmPackage
import dev.petuska.npm.publish.extension.domain.NpmPackages
import resources.ResourceValidation.Companion.resourceValidation

plugins {
    id("base")
    id("build_logic")
    alias(libs.plugins.publish.npm)
}

// Have to have as a standalone gradle module so that the publication
// task is not created for the :library:binary module's js target. All
// that is being done here is publication of resources so that they can
// be consumed as a npm dependency from :library:binary module.
npmPublish {
    val npmjsAuthToken = rootProject.findProperty("NPMJS_AUTH_TOKEN") as? String
    if (npmjsAuthToken.isNullOrBlank()) return@npmPublish

//    dry.set(true)

    registries {
        npmjs {
            authToken.set(npmjsAuthToken)
        }
    }

    resourceValidation {
        torResources {

            val jvmGeoipSrcDir = jvmGeoipResourcesSrcDir()
            val jvmTorLibsSrcDir = jvmTorLibResourcesSrcDir()

            packages {
                val snapshotVersion = properties["NPMJS_SNAPSHOT_VERSION"]!!
                    .toString()
                    .toInt()

                check(snapshotVersion >= 0) {
                    "NPMJS_SNAPSHOT_VERSION cannot be negative"
                }

                val vProject = "${project.version}"
                if (vProject.endsWith("-SNAPSHOT")) {

                    // Only register snapshot task when project version is -SNAPSHOT
                    registerBinaryResources(
                        releaseVersion = "$vProject.$snapshotVersion",
                        geoipResourcesDir = jvmGeoipSrcDir,
                        torResourcesDir = jvmTorLibsSrcDir,
                    )
                } else {
                    check(snapshotVersion == 0) {
                        "NPMJS_SNAPSHOT_VERSION must be 0 for releases"
                    }

                    // Release will be X.X.X-#
                    // Increment the # for the next SNAPSHOT version
                    val increment = vProject.last().toString().toInt() + 1
                    val nextVersion = vProject
                        .substringBefore('-') +
                            "-$increment"

                    // Register both snapshot and release tasks when project
                    // version indicates a release so after maven publication
                    // and git tagging, updating VERSION_NAME with -SNAPSHOT
                    // there will be a "next release" waiting
                    registerBinaryResources(
                        releaseVersion = vProject,
                        geoipResourcesDir = jvmGeoipSrcDir,
                        torResourcesDir = jvmTorLibsSrcDir,
                    )
                    registerBinaryResources(
                        releaseVersion = "$nextVersion-SNAPSHOT.0",
                        geoipResourcesDir = jvmGeoipSrcDir,
                        torResourcesDir = jvmTorLibsSrcDir,
                    )
                }
            }
        }
    }

    // TODO: Make Npmjs publication tasks dependant on the jvm resource validation tasks
}

fun NpmPackages.registerBinaryResources(
    releaseVersion: String,
    geoipResourcesDir: File,
    torResourcesDir: File
) {
    val name = if (releaseVersion.contains("SNAPSHOT")) {
        "binary-resources-snapshot"
    } else {
        "binary-resources-release"
    }

    register(name) {
        packageName.set("${rootProject.name}-resources")
        version.set(releaseVersion)

        main.set("index.js")
        readme.set(projectDir.resolve("README.md"))

        files {
            from("index.js")
            from(geoipResourcesDir)
            from(torResourcesDir)
        }

        packageInfoJson()
    }
}

fun NpmPackage.packageInfoJson() {
    packageJson {
        homepage.set("https://github.com/05nelsonm/${rootProject.name}")
        license.set("Apache 2.0")

        repository {
            type.set("git")
            url.set("git+https://github.com/05nelsonm/${rootProject.name}.git")
        }
        author {
            name.set("Matthew Nelson")
        }
        bugs {
            url.set("https://github.com/05nelsonm/${rootProject.name}/issues")
        }

        keywords.add("tor")
        keywords.add("kmp-tor")
    }
}

tasks.getByName("clean") {
    projectDir.resolve("build").delete()
}

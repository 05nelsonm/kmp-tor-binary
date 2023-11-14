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

plugins {
    alias(libs.plugins.publish.npm)
}

// Have to have as a standalone gradle module so that the publication
// task is not created for the :library:binary module's js target. All
// that is being done here is publication of resources so that they can
// be consumed as a npm dependency from :library:binary module.
npmPublish {
    val npmjsAuthToken = rootProject.findProperty("NPMJS_AUTH_TOKEN") as? String
    if (npmjsAuthToken.isNullOrBlank()) return@npmPublish

    registries {
        npmjs {
            authToken.set(npmjsAuthToken)
        }
    }

    packages {
        register("binary") {
            packageName.set(rootProject.name)
            version.set("${project.version}")

            main.set("index.js")
            readme.set(projectDir.resolve("README.md"))

            files {
                val binarySrc = projectDir
                    .resolveSibling("binary")
                    .resolve("src")

                // geoip resources
                from(binarySrc.resolve("jvmAndroidMain").resolve("resources"))
                // tor binary resources
                from(binarySrc.resolve("jvmMain").resolve("resources"))
            }

            packageInfoJson()
        }
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

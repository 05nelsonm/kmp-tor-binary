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
import dev.petuska.npm.publish.dsl.NpmAccess
import dev.petuska.npm.publish.dsl.NpmPublishExtension

plugins {
    id("dev.petuska.npm.publish")
}

// Include in project module's gradle.properties file
// NPM_RES_SOURCE_SET=<path>
// e.g.
//   NPM_RES_SOURCE_SET=commonMain
val npmResourcePath: String = findProperty("NPM_RES_SOURCE_SET")
    ?.toString()
    ?.let {
        if (it.contains('/')) {
            throw GradleException("NPM_RES_SOURCE_SET cannot contain directory separators")
        }

        "$projectDir/src/$it/resources"
    }
    ?: throw GradleException("NPM_RES_SOURCE_SET not found in $project/gradle.properties")

extensions.configure<NpmPublishExtension>("npmPublishing") {
    publications {
        publication(project.name) {
            description = project.description
            main = "index.js"
            access = NpmAccess.PUBLIC
            version = project.version.toString()
            readme = file("README.md")
            packageJson {
                homepage = "https://github.com/05nelsonm/${rootProject.name}"
                license = "Apache 2.0"
                repository {
                    type = "git"
                    url = "git+https://github.com/05nelsonm/${rootProject.name}.git"
                }
                author {
                    name = "Matthew Nelson"
                }
                bugs {
                    url = "https://github.com/05nelsonm/${rootProject.name}/issues"
                }
            }

            repositories {
                val port = rootProject.findProperty("NPMJS_VERDACCIO_PORT") as? String
                val token = rootProject.findProperty("NPMJS_VERDACCIO_AUTH_TOKEN") as? String

                if (port != null && token != null) {
                    repository("verdaccio") {
                        registry = uri("http://localhost:$port")
                        authToken = token
                    }
                }

                repository("npmjs") {
                    registry = uri("https://registry.npmjs.org")
                    authToken = rootProject.findProperty("NPMJS_AUTH_TOKEN") as? String
                }
            }

            files {
                from(projectDir) {
                    include("index.js")
                }
                from(npmResourcePath) {
                    include("kmptor/**")
                }
            }
            packageJson {
                keywords = jsonArray("tor", "kmp-tor")
            }
        }
    }
}

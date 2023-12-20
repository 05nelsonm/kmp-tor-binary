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
plugins {
    id("configuration")
}

repositories {
    val host = "https://s01.oss.sonatype.org"

    if (version.toString().endsWith("-SNAPSHOT")) {
        maven("$host/content/repositories/snapshots/")
    } else {
        maven("$host/content/groups/staging") {
            val p = rootProject.properties

            credentials {
                username = p["mavenCentralUsername"]?.toString()
                password = p["mavenCentralPassword"]?.toString()
            }
        }
    }
}

kmpConfiguration {
    configureShared(androidNamespace = "tools.check.publication") {
        androidLibrary {
            sourceSetMain {
                dependencies {
                    // Should be a SEPARATE publication from binary-jvm
                    implementation("$group:binary-android:$version")
                    implementation("$group:binary-initializer:$version")
                }
            }
            sourceSetTest {
                dependencies {
                    implementation("$group:binary-android-unit-test:$version")
                }
            }
        }

        jvm {
            sourceSetMain {
                dependencies {
                    implementation("$group:binary-jvm:$version")
                }
            }
        }

        iosAll()
        tvosAll()
        watchosAll()

        common {
            sourceSetMain {
                dependencies {
                    implementation("$group:binary-core:$version")
                    implementation("$group:binary-core-api:$version")
                }
            }
        }

        kotlin {
            with(sourceSets) {
                // binary is not available for iOS/tvOS/watchOS
                // currently, so cannot add to commonMain.
                listOf(
                    findByName("jvmAndroidMain"),
                    findByName("jsMain"),
                    findByName("linuxMain"),
                    findByName("macosMain"),
                    findByName("mingwMain"),
                ).forEach { sourceSet ->
                    sourceSet?.dependencies {
                        implementation("$group:binary:$version")
                    }
                }
            }
        }
    }
}

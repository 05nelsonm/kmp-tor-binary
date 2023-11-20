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

kmpConfiguration {
    configureShared(
        androidNamespace = "io.matthewnelson.kmp.tor.binary",
        publish = true,
    ) {
        androidLibrary {
            android {
                packaging {
                    jniLibs.useLegacyPackaging = true
                }

                defaultConfig {
                    ndk {
                        abiFilters.add("arm64-v8a")
                        abiFilters.add("armeabi-v7a")
                        abiFilters.add("x86")
                        abiFilters.add("x86_64")
                    }

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                sourceSets.getByName("main") {
                    jniLibs.srcDir("src/androidMain/jniLibs")

                    // geoip/geoip6 files
                    resources.srcDirs("src/jvmAndroidMain/resources")
                }
            }

            sourceSetMain {
                dependencies {
                    implementation(project(":library:binary-initializer"))
                }
            }

            sourceSetTest {
                dependencies {
                    implementation(project(":library:binary-android-unit-test"))
                }
            }

            sourceSetTestInstrumented {
                dependencies {
                    implementation(libs.androidx.test.core)
                    implementation(libs.androidx.test.runner)
                }
            }
        }

        js {
            sourceSetMain {
                dependencies {
                    val npmVersion = if ("$version".endsWith("-SNAPSHOT")) {
                        val snapshotVersion = properties["NPMJS_SNAPSHOT_VERSION"]!!
                            .toString()
                            .toInt()

                        "$version.$snapshotVersion"
                    } else {
                        // If project version is not SNAPSHOT, this
                        // will inhibit releasing to MavenCentral w/o
                        // firsting making a release publication to
                        // Npmjs of the resources.
                        "$version"
                    }

                    implementation(npm("kmp-tor-binary-resources", npmVersion))
                }
            }

            sourceSetTest {
                dependencies {
                    implementation(libs.okio.node)
                }
            }
        }

        common {
            sourceSetMain {
                dependencies {
                    implementation(project(":library:binary-core"))
                }
            }

            sourceSetTest {
                dependencies {
                    implementation(libs.okio.okio)
                    implementation(libs.encoding.base16)
                }
            }
        }

        kotlin {
            with(sourceSets) {
                val jsMain = findByName("jsMain")
                val jvmAndroidMain = findByName("jvmAndroidMain")

                if (jsMain != null || jvmAndroidMain != null) {
                    val nonNativeMain = maybeCreate("nonNativeMain")
                    nonNativeMain.dependsOn(getByName("commonMain"))
                    jvmAndroidMain?.apply { dependsOn(nonNativeMain) }
                    jsMain?.apply { dependsOn(nonNativeMain) }

                    val nonNativeTest = maybeCreate("nonNativeTest")
                    nonNativeTest.dependsOn(getByName("commonTest"))
                    findByName("jvmAndroidTest")?.apply { dependsOn(nonNativeTest) }
                    findByName("jsTest")?.apply { dependsOn(nonNativeTest) }
                }
            }
        }
    }
}

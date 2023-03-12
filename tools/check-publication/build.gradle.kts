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
    configureShared(
        androidNameSpace = "io.matthewnelson.kmp.tor.binary.tools.check.publication",
        explicitApi = false,
    ) {
        jvm {
            sourceSetMain {
                dependencies {
                    implementation("$group:kmp-tor-binary-extract:$version")
                    implementation("$group:kmp-tor-binary-geoip:$version")

                    implementation("$group:kmp-tor-binary-linuxx64:$version")
                    implementation("$group:kmp-tor-binary-linuxx86:$version")
                    implementation("$group:kmp-tor-binary-macosx64:$version")
                    implementation("$group:kmp-tor-binary-macosarm64:$version")
                    implementation("$group:kmp-tor-binary-mingwx64:$version")
                    implementation("$group:kmp-tor-binary-mingwx86:$version")
                }
            }
        }

        androidLibrary {
            android {
                defaultConfig {
                    minSdk = 21
                }
            }
            sourceSetMain {
                dependencies {
                    implementation("$group:kmp-tor-binary-android:$version")
                    implementation("$group:kmp-tor-binary-extract:$version")
                    implementation("$group:kmp-tor-binary-geoip:$version")
                }
            }
        }

        js {
            target {
                nodejs {
                    testTask {
                        useMocha { timeout = "30s" }
                    }
                }
            }

            sourceSetMain {
                dependencies {
                    implementation("$group:kmp-tor-binary-extract:$version")

                    implementation(npm("kmp-tor-binary-geoip", "$version"))
                    implementation(npm("kmp-tor-binary-linuxx64", "$version"))
                    implementation(npm("kmp-tor-binary-linuxx86", "$version"))
                    implementation(npm("kmp-tor-binary-macosx64", "$version"))
                    implementation(npm("kmp-tor-binary-macosarm64", "$version"))
                    implementation(npm("kmp-tor-binary-mingwx64", "$version"))
                    implementation(npm("kmp-tor-binary-mingwx86", "$version"))
                }
            }
        }

        linuxX64 {
            sourceSetMain {
                dependencies {
//                    implementation("$group:kmp-tor-binary-linuxx64:$version")
                }
            }
        }

        macosX64 {
            sourceSetMain {
                dependencies {
//                    implementation("$group:kmp-tor-binary-macosx64:$version")
                }
            }
        }

        macosArm64 {
            sourceSetMain {
                dependencies {
//                    implementation("$group:kmp-tor-binary-macosarm64:$version")
                }
            }
        }

        mingwX64 {
            sourceSetMain {
                dependencies {
//                    implementation("$group:kmp-tor-binary-mingwx64:$version")
                }
            }
        }

        common {
            sourceSetMain {
                dependencies {
//                    implementation("$group:kmp-tor-binary-extract:$version")
//                    implementation("$group:kmp-tor-binary-geoip:$version")
                }
            }
        }
    }
}

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
        androidNameSpace = "io.matthewnelson.kmp.tor.binary.extract",
        publish = true,
    ) {
        androidLibrary {
            sourceSetMain {
                dependencies {
                    compileOnly(project(":library:kmp-tor-binary-geoip"))
                }
            }
        }

        jvm {
            sourceSetTest {
                dependencies {
                    implementation(project(":library:kmp-tor-binary-linuxx64"))
                    implementation(project(":library:kmp-tor-binary-linuxx86"))
                    implementation(project(":library:kmp-tor-binary-macosx64"))
                    implementation(project(":library:kmp-tor-binary-macosarm64"))
                    implementation(project(":library:kmp-tor-binary-mingwx64"))
                    implementation(project(":library:kmp-tor-binary-mingwx86"))
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

            sourceSetTest {
                dependencies {

                    implementation(libs.okio.okio)
                    implementation(libs.okio.node)

//                    implementation(npm("kmp-tor-binary-geoip", "$version"))
//                    implementation(npm("kmp-tor-binary-linuxx64", "$version"))
//                    implementation(npm("kmp-tor-binary-linuxx86", "$version"))
//                    implementation(npm("kmp-tor-binary-macosarm64", "$version"))
//                    implementation(npm("kmp-tor-binary-macosx64", "$version"))
//                    implementation(npm("kmp-tor-binary-mingwx64", "$version"))
//                    implementation(npm("kmp-tor-binary-mingwx86", "$version"))
                }
            }
        }

        linuxX64()
        mingwX64()

        iosAll()
        macosAll()
        tvosAll()
        watchosAll()

        common {
            sourceSetTest {
                dependencies {
                    implementation(kotlin("test"))
                    implementation(libs.kotlincrypto.hash.sha2)
                    implementation(libs.encoding.base16)
                }
            }
        }

        kotlin {
            with(sourceSets) {
                findByName("nativeTest")?.apply {
                    dependencies {
                        implementation(project(":library:kmp-tor-binary-geoip"))
                    }
                }
                findByName("jvmAndroidTest")?.apply {
                    dependencies {
                        implementation(project(":library:kmp-tor-binary-geoip"))
                    }
                }

                val jvmMain = findByName("jvmMain")
                val jsMain = findByName("jsMain")

                if (jvmMain != null || jsMain != null) {
                    val commonMain by getting
                    val commonTest by getting

                    val jvmJsMain = maybeCreate("jvmJsMain").apply {
                        dependsOn(commonMain)
                    }
                    val jvmJsTest = maybeCreate("jvmJsTest").apply {
                        dependsOn(commonTest)
                    }

                    jvmMain?.apply { dependsOn(jvmJsMain) }
                    findByName("jvmTest")?.apply { dependsOn(jvmJsTest) }

                    jsMain?.apply { dependsOn(jvmJsMain) }
                    findByName("jsTest")?.apply { dependsOn(jvmJsTest) }
                }
            }
        }
    }
}

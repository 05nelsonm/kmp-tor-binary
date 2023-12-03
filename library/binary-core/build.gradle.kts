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
    configureShared(publish = true) {

        js {
            sourceSetTest {
                dependencies {
                    implementation(libs.okio.node)
                }
            }
        }

        iosAll()
//        linuxAll()
        macosAll()
        tvosAll()
        watchosArm32()
        watchosArm64()
        // Not supported by Okio (used for tests)
        // See https://github.com/square/okio/issues/1381
//        watchosDeviceArm64()
        watchosX64()
        watchosSimulatorArm64()

        common {
            sourceSetTest {
                dependencies {
                    implementation(kotlin("test"))
                    implementation(libs.okio.okio)
                }
            }
        }

        kotlin {
            with(sourceSets) {
                val jsMain = findByName("jsMain")
                val jvmMain = findByName("jvmMain")

                if (jsMain != null || jvmMain != null) {
                    val nonNativeMain = maybeCreate("nonNativeMain")
                    nonNativeMain.dependsOn(getByName("commonMain"))
                    jvmMain?.apply { dependsOn(nonNativeMain) }
                    jsMain?.apply { dependsOn(nonNativeMain) }
                }

                findByName("nativeMain")?.apply {
                    dependencies {
                        implementation(libs.encoding.base16)
                        implementation(libs.encoding.base64)
                        implementation(libs.kotlincrypto.hash.sha2)
                    }
                }
            }
        }
    }
}

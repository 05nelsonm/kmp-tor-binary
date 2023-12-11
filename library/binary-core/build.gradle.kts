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

private val testConfig = TestConfigInject()

kmpConfiguration {
    configureShared(publish = true) {
        jvm {
            sourceSetTest {
                dependencies {
                    implementation(project(":tools:resource-cli"))
                }
            }
        }

        common {
            sourceSetMain {
                dependencies {
                    api(libs.kmp.file)
                }
            }

            sourceSetTest {
                kotlin.srcDir(testConfig.testConfigSrcDir)

                dependencies {
                    implementation(kotlin("test"))
                    implementation(libs.encoding.base16)
                    implementation(libs.kotlincrypto.hash.sha2)
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
                        implementation(libs.kotlinx.atomicfu)
                    }
                }

                // TODO: If jvm enabled, native tests depend on jvmTest
                //  running which will write the test_support/lorem_ipsum.txt
                //  resource to nativeTest
            }
        }
    }
}

private class TestConfigInject {
    // Inject project directory path for tests
    val testConfigSrcDir: File by lazy {
        val kotlinSrcDir = layout
            .buildDirectory
            .get()
            .asFile
            .resolve("generated")
            .resolve("sources")
            .resolve("testConfig")
            .resolve("commonTest")
            .resolve("kotlin")

        val core = kotlinSrcDir
            .resolve("io")
            .resolve("matthewnelson")
            .resolve("kmp")
            .resolve("tor")
            .resolve("binary")
            .resolve("core")

        core.mkdirs()

        core.resolve("TestConfig.kt").writeText(
"""package io.matthewnelson.kmp.tor.binary.core

internal const val PROJECT_DIR_PATH: String = "${projectDir.canonicalPath.replace("\\", "\\\\")}"
"""
        )

        kotlinSrcDir
    }
}

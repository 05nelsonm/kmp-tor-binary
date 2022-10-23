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
import io.matthewnelson.kotlin.components.dependencies.deps
import io.matthewnelson.kotlin.components.dependencies.versions
import io.matthewnelson.kotlin.components.kmp.KmpTarget
import io.matthewnelson.kotlin.components.kmp.publish.kmpPublishRootProjectConfiguration
import io.matthewnelson.kotlin.components.kmp.util.sourceSetJvmAndroidTest
import io.matthewnelson.kotlin.components.kmp.util.sourceSetNativeTest
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

plugins {
    id(pluginId.kmp.configuration)
    id(pluginId.kmp.publish)
}

kmpConfiguration {
    setupMultiplatform(
        setOf(

            KmpTarget.Jvm.Jvm(
                testSourceSet = {
                    dependencies {
                        implementation(project(":library:kmp-tor-binary-linuxx64"))
                        implementation(project(":library:kmp-tor-binary-linuxx86"))
                        implementation(project(":library:kmp-tor-binary-macosx64"))
                        implementation(project(":library:kmp-tor-binary-macosarm64"))
                        implementation(project(":library:kmp-tor-binary-mingwx64"))
                        implementation(project(":library:kmp-tor-binary-mingwx86"))
                    }
                }
            ),

            KmpTarget.Jvm.Android(
                buildTools = versions.android.buildTools,
                compileSdk = versions.android.sdkCompile,
                minSdk = versions.android.sdkMin16,
                target = {
                    publishLibraryVariants("release")
                },
                mainSourceSet = {
                    dependencies {
                        compileOnly(project(":library:kmp-tor-binary-geoip"))
                    }
                },
                testSourceSet = {
                    dependencies {
                        implementation(project(":library:kmp-tor-binary-geoip"))
                    }
                }
            ),

            KmpTarget.NonJvm.JS(
                compilerType = KotlinJsCompilerType.BOTH,
                browser = null,
                node = KmpTarget.NonJvm.JS.Node(),
                testSourceSet = {
                    dependencies {
                        implementation(deps.square.okio.okio)
                        implementation(deps.square.okio.nodeFileSys)

                        // TODO: Uncomment upon release. Cannot merge to master b/c
                        //  have not published to npmjs.
//                        val versionName = kmpPublishRootProjectConfiguration?.versionName!!
//
//                        implementation(npm("kmp-tor-binary-geoip", versionName))
//                        implementation(npm("kmp-tor-binary-linuxx64", versionName))
//                        implementation(npm("kmp-tor-binary-linuxx86", versionName))
//                        implementation(npm("kmp-tor-binary-macosarm64", versionName))
//                        implementation(npm("kmp-tor-binary-macosx64", versionName))
//                        implementation(npm("kmp-tor-binary-mingwx64", versionName))
//                        implementation(npm("kmp-tor-binary-mingwx86", versionName))
                    }
                }
            ),

            KmpTarget.NonJvm.Native.Unix.Linux.X64.DEFAULT,

            KmpTarget.NonJvm.Native.Mingw.X64.DEFAULT,
        ) +
        KmpTarget.NonJvm.Native.Unix.Darwin.Ios.ALL_DEFAULT     +
        KmpTarget.NonJvm.Native.Unix.Darwin.Macos.ALL_DEFAULT   +
        KmpTarget.NonJvm.Native.Unix.Darwin.Tvos.ALL_DEFAULT    +
        KmpTarget.NonJvm.Native.Unix.Darwin.Watchos.ALL_DEFAULT,

        commonTestSourceSet = {
            dependencies {
                implementation(kotlin("test"))
                implementation(deps.components.encoding.base16)
            }
        },

        kotlin = {
            sourceSetJvmAndroidTest {
                dependencies {
                    implementation(project(":library:kmp-tor-binary-geoip"))
                }
            }
            sourceSetNativeTest {
                dependencies {
                    implementation(project(":library:kmp-tor-binary-geoip"))
                }
            }
        }
    )
}

kmpPublish {
    setupModule(
        pomDescription = "Kotlin Components' TorBinary resource extraction distribution",
    )
}

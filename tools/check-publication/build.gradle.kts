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
import io.matthewnelson.kotlin.components.dependencies.versions
import io.matthewnelson.kotlin.components.kmp.KmpTarget
import io.matthewnelson.kotlin.components.kmp.publish.kmpPublishRootProjectConfiguration
import io.matthewnelson.kotlin.components.kmp.util.sourceSetJvmJsCommonMain
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

plugins {
    id("kmp-configuration")
}

val pConfig = kmpPublishRootProjectConfiguration!!

repositories {
    if (pConfig.versionName.endsWith("-SNAPSHOT")) {
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    } else {
        maven("https://oss.sonatype.org/content/groups/staging") {
            credentials {
                username = ext.get("mavenCentralUsername").toString()
                password = ext.get("mavenCentralPassword").toString()
            }
        }
    }
}

kmpConfiguration {
    setupMultiplatform(
        setOf(

            KmpTarget.Jvm.Jvm(
                mainSourceSet = {
                    dependencies {
                        implementation("${pConfig.group}:kmp-tor-binary-extract:${pConfig.versionName}")
                        implementation("${pConfig.group}:kmp-tor-binary-geoip:${pConfig.versionName}")

                        // TODO: Remove once JS is published
                        implementation("${pConfig.group}:kmp-tor-binary-linuxx64:${pConfig.versionName}")
                        implementation("${pConfig.group}:kmp-tor-binary-linuxx86:${pConfig.versionName}")
                        implementation("${pConfig.group}:kmp-tor-binary-macosx64:${pConfig.versionName}")
                        implementation("${pConfig.group}:kmp-tor-binary-mingwx64:${pConfig.versionName}")
                        implementation("${pConfig.group}:kmp-tor-binary-mingwx86:${pConfig.versionName}")
                    }
                }
            ),

            KmpTarget.Jvm.Android(
                buildTools = versions.android.buildTools,
                compileSdk = versions.android.sdkCompile,
                minSdk = versions.android.sdkMin16,
                mainSourceSet = {
                    dependencies {
                        implementation("${pConfig.group}:kmp-tor-binary-android:${pConfig.versionName}")
                        implementation("${pConfig.group}:kmp-tor-binary-extract:${pConfig.versionName}")
                        implementation("${pConfig.group}:kmp-tor-binary-geoip:${pConfig.versionName}")
                    }
                },
            ),

            KmpTarget.NonJvm.JS(
                compilerType = KotlinJsCompilerType.BOTH,
                browser = null,
                node = KmpTarget.NonJvm.JS.Node(
                    jsNodeDsl = null
                )
            ),

//            KmpTarget.NonJvm.Native.Unix.Darwin.Ios.All.DEFAULT,
//            KmpTarget.NonJvm.Native.Unix.Darwin.Macos.Arm64.DEFAULT,
            KmpTarget.NonJvm.Native.Unix.Darwin.Macos.X64(
                mainSourceSet = {
                    dependencies {
//                        implementation("${pConfig.group}:kmp-tor-binary-macosx64:${pConfig.versionName}")
                    }
                },
            ),
//            KmpTarget.NonJvm.Native.Unix.Darwin.Tvos.All.DEFAULT,
//            KmpTarget.NonJvm.Native.Unix.Darwin.Watchos.All.DEFAULT,
            KmpTarget.NonJvm.Native.Unix.Linux.X64(
                mainSourceSet = {
                    dependencies {
//                        implementation("${pConfig.group}:kmp-tor-binary-linuxx64:${pConfig.versionName}")
                    }
                },
            ),
            KmpTarget.NonJvm.Native.Mingw.X64(
                mainSourceSet = {
                    dependencies {
//                        implementation("${pConfig.group}:kmp-tor-binary-mingwx64:${pConfig.versionName}")
                    }
                },
            ),
        ),
        commonMainSourceSet = {
            dependencies {
                // TODO: Uncomment once all targets are published.
//                implementation("${pConfig.group}:kmp-tor-binary-extract:${pConfig.versionName}")
//                implementation("${pConfig.group}:kmp-tor-binary-geoip:${pConfig.versionName}")
            }
        },
        kotlin = {
            sourceSetJvmJsCommonMain {
                dependencies {
                    // TODO: Uncomment once JS is published
//                    implementation("${pConfig.group}:kmp-tor-binary-linuxx64:${pConfig.versionName}")
//                    implementation("${pConfig.group}:kmp-tor-binary-linuxx86:${pConfig.versionName}")
//                    implementation("${pConfig.group}:kmp-tor-binary-macosx64:${pConfig.versionName}")
//                    implementation("${pConfig.group}:kmp-tor-binary-mingwx64:${pConfig.versionName}")
//                    implementation("${pConfig.group}:kmp-tor-binary-mingwx86:${pConfig.versionName}")
                }
            }
        }
    )
}

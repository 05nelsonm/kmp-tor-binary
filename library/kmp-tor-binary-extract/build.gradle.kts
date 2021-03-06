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
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

plugins {
    id(pluginId.kmp.configuration)
    id(pluginId.kmp.publish)
}

kmpConfiguration {
    setupMultiplatform(
        setOf(

            KmpTarget.Jvm.Jvm.DEFAULT,

            KmpTarget.Jvm.Android(
                buildTools = versions.android.buildTools,
                compileSdk = versions.android.sdkCompile,
                minSdk = versions.android.sdkMin16,
                target = {
                    publishLibraryVariants("release")
                },
            ),

            KmpTarget.NonJvm.JS(
                compilerType = KotlinJsCompilerType.BOTH,
                browser = null,
                node = KmpTarget.NonJvm.JS.Node(),
            ),

//            KmpTarget.NonJvm.Native.Unix.Darwin.Ios.Arm32.DEFAULT,
//            KmpTarget.NonJvm.Native.Unix.Darwin.Ios.Arm64.DEFAULT,
//            KmpTarget.NonJvm.Native.Unix.Darwin.Ios.X64.DEFAULT,
//            KmpTarget.NonJvm.Native.Unix.Darwin.Ios.SimulatorArm64.DEFAULT,

            KmpTarget.NonJvm.Native.Unix.Darwin.Macos.X64.DEFAULT,
//            KmpTarget.NonJvm.Native.Unix.Darwin.Macos.Arm64.DEFAULT,

//            KmpTarget.NonJvm.Native.Unix.Darwin.Tvos.Arm64.DEFAULT,
//            KmpTarget.NonJvm.Native.Unix.Darwin.Tvos.X64.DEFAULT,
//            KmpTarget.NonJvm.Native.Unix.Darwin.Tvos.SimulatorArm64.DEFAULT,
//
//            KmpTarget.NonJvm.Native.Unix.Darwin.Watchos.Arm32.DEFAULT,
//            KmpTarget.NonJvm.Native.Unix.Darwin.Watchos.Arm64.DEFAULT,
//            KmpTarget.NonJvm.Native.Unix.Darwin.Watchos.X64.DEFAULT,
//            KmpTarget.NonJvm.Native.Unix.Darwin.Watchos.X86.DEFAULT,
//            KmpTarget.NonJvm.Native.Unix.Darwin.Watchos.SimulatorArm64.DEFAULT,

            KmpTarget.NonJvm.Native.Unix.Linux.X64.DEFAULT,

            KmpTarget.NonJvm.Native.Mingw.X64.DEFAULT,
        ),
        commonTestSourceSet = {
            dependencies {
                implementation(kotlin("test"))
                implementation(deps.components.encoding.base16)
            }
        }
    )
}

kmpPublish {
    setupModule(
        pomDescription = "Kotlin Components' TorBinary resource extraction distribution",
    )
}

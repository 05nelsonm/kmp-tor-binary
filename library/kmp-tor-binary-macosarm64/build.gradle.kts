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
import io.matthewnelson.kotlin.components.kmp.KmpTarget
import io.matthewnelson.kotlin.components.kmp.util.npmPublish

plugins {
    id(pluginId.kmp.configuration)
    id(pluginId.kmp.publish)
    id(pluginId.npmPublish)
}

kmpConfiguration {
    setupMultiplatform(
        setOf(

            KmpTarget.Jvm.Jvm.DEFAULT,

            KmpTarget.NonJvm.Native.Unix.Darwin.Macos.Arm64.DEFAULT,
        )
    )
}

kmpPublish {
    setupModule(
        pomDescription = "Kotlin Components' TorBinary distribution for macOS arm64",
    )
}

npmPublish {
    description = "npm distribution of macOS arm64 Tor binary files for the kmp-tor project"
    files {
        from(projectDir) {
            include("index.js")
        }
        from("$projectDir/src/jvmMain/resources") {
            include("kmptor/**")
        }
    }
    packageJson {
        keywords = jsonArray("tor", "kmp-tor")
    }
}

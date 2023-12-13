/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package resources

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

@ResourceDsl
class TorResources internal constructor(
    project: Project
): ResourceValidation(
    project = project,
    moduleName = "binary",
    modulePackageName = "io.matthewnelson.kmp.tor.binary",
) {

    private val geoipErrors = mutableSetOf<String>()
    private var isGeoipConfigured = false

    @ResourceDsl
    fun LibraryExtension.configureTorJniResources() {
        configureAndroidJniResources()
    }

    @ResourceDsl
    fun jvmTorLibResourcesSrcDir(): File = jvmLibResourcesSrcDir()

    @ResourceDsl
    fun KotlinMultiplatformExtension.configureTorNativeResources() {
        configureNativeResources()
    }

    @ResourceDsl
    fun jvmGeoipResourcesSrcDir(): File {
        val mockResourcesSrc = rootProjectDir
            .resolve("library")
            .resolve(moduleName)
            .resolve("mock-resources")
            .resolve("src")
            .resolve("jvmAndroidMain")
            .resolve("resources")

        val externalResourcesSrc = rootProjectDir
            .resolve("external")
            .resolve("build")
            .resolve("package")
            .resolve(moduleName)
            .resolve("src")
            .resolve("jvmAndroidMain")
            .resolve("resources")

        if (isGeoipConfigured) {
            return if (geoipErrors.isEmpty()) {
                externalResourcesSrc
            } else {
                mockResourcesSrc
            }
        }

        val binaryResourcesDir = externalResourcesSrc
            .resolve(modulePackageName.replace('.', '/'))

        jvmGeoipHashes.forEach { (name, hash) ->
            val file = binaryResourcesDir.resolve(name)

            if (!file.exists()) {
                geoipErrors.add("$name does not exist: $file")
                return@forEach
            }

            val actualHash = file.sha256()
            if (hash != actualHash) {
                geoipErrors.add("$name hash[$actualHash] did not match expected[$hash]: $file")
            }
        }

        isGeoipConfigured = true
        generateReport(reportFileName = "jvm-geoip", errors = geoipErrors)

        return if (geoipErrors.isEmpty()) {
            externalResourcesSrc
        } else {
            mockResourcesSrc
        }
    }

    internal fun build(): Set<String> = geoipErrors

    private val jvmGeoipHashes = setOf(
        "geoip.gz" to "c817b735fadf80211b34c8e61c71aaf8824d12de177417de8346b4f589746ab2",
        "geoip6.gz" to "7f398d48ba2ad81a18d6a056f6edd6bd18a368380e01a17cbba5c188c2407db1",
    )

    override val androidLibHashes = setOf(
        AndroidLibHash(
            libname = "libtor.so",
            hashArm64 = "37b4a53e70ba16fa8b83219b127d47fbd90c33e60f26d0e2bfd7e978a4008506",
            hashArmv7 = "fc6f8c21976c2e17c1fbef24d274a49e403b4d1465d86b3c27b97afbba7fed65",
            hashX86 = "6147d0445750f0d938de08898f16a8a22fcab59d6fac80494731b54ca947db65",
            hashX86_64 = "9bdd3dbd6dcd7cacb643d0be0f278f7b310b62f5756ac7f03fba14ab90bcbd55",
        ),
    )

    override val jvmLibHashes = setOf(
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-android",
            arch = "aarch64",
            hash = "60b8b15cff91b52ff50719dada4a0429e9387321f5c1832afa63c87ca99a9955",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-android",
            arch = "armv7",
            hash = "24dbee119ebc1a96a75c0a1d89c869c7f217e3b7137513a2bc106cbdf9565354",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-android",
            arch = "x86",
            hash = "8e364896f3e2c4a884e62ad0d0c5c63cc906c791dcd697f6e6cb2fae02da03c9",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-android",
            arch = "x86_64",
            hash = "80b328f344d4445479c15ac338fc18c92769486f05fac25aa91b0042dd693083",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-libc",
            arch = "aarch64",
            hash = "0a845e75ab0172555db234b69486b3e081a83977ab3bd7de5943185602a347ad",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-libc",
            arch = "armv7",
            hash = "2cf567789dbfe49a6dc6d6a0b1b48658a649d18a1b3ec1ad63c65dfb2b1988c9",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-libc",
            arch = "x86",
            hash = "c474b9cb85c67ac8cd96d2020095469cef3950600151a36a321e92d881ab6f97",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-libc",
            arch = "x86_64",
            hash = "b5d46f497c271f57c703ae1651749fb88aa0dc36a92fc67a8c947c692d5dd651",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "macos",
            arch = "aarch64",
            hash = "80b9a3b818eb556aa8c0240ce92ad950d94dae79cb4a56c7a42f55acd8a79999",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "macos",
            arch = "x86_64",
            hash = "98dc35755653c698fc20dbf835c17ad8a358d914954b94754a82d88d36623f93",
        ),
        JvmLibHash(
            libname = "tor.exe.gz",
            machine = "mingw",
            arch = "x86",
            hash = "a83f2a43bbea0ce0e630bdbe7545dc71b25780602b3a807944b45e20eda94681",
        ),
        JvmLibHash(
            libname = "tor.exe.gz",
            machine = "mingw",
            arch = "x86_64",
            hash = "c2eb477ff1de2dce9f93f5e202b9d5094d1a9d821295f1806fd5a6bee61a810e",
        ),
    )

    override val nativeResourceHashes: Set<NativeResourceHash> = setOf(
        NativeResourceHash(
            sourceSetName = "ios",
            ktFileName = "resource_tor_gz.kt",
            hash = "TODO"
        ),
        NativeResourceHash(
            sourceSetName = "linuxArm64",
            ktFileName = "resource_tor_gz.kt",
            hash = "0a845e75ab0172555db234b69486b3e081a83977ab3bd7de5943185602a347ad"
        ),
        NativeResourceHash(
            sourceSetName = "linuxX64",
            ktFileName = "resource_tor_gz.kt",
            hash = "b5d46f497c271f57c703ae1651749fb88aa0dc36a92fc67a8c947c692d5dd651"
        ),
        NativeResourceHash(
            sourceSetName = "macos",
            ktFileName = "resource_tor_gz.kt",
            hash = "TODO"
        ),
        NativeResourceHash(
            sourceSetName = "mingwX64",
            ktFileName = "resource_tor_exe_gz.kt",
            hash = "c2eb477ff1de2dce9f93f5e202b9d5094d1a9d821295f1806fd5a6bee61a810e"
        ),
        NativeResourceHash(
            sourceSetName = "native",
            ktFileName = "resource_geoip6_gz.kt",
            hash = "7f398d48ba2ad81a18d6a056f6edd6bd18a368380e01a17cbba5c188c2407db1"
        ),
        NativeResourceHash(
            sourceSetName = "native",
            ktFileName = "resource_geoip_gz.kt",
            hash = "c817b735fadf80211b34c8e61c71aaf8824d12de177417de8346b4f589746ab2"
        ),
        NativeResourceHash(
            sourceSetName = "tvos",
            ktFileName = "resource_tor_gz.kt",
            hash = "TODO"
        ),
        NativeResourceHash(
            sourceSetName = "watchos",
            ktFileName = "resource_tor_gz.kt",
            hash = "TODO"
        ),
    )
}
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
            hashArm64 = "b03cf9818b879d9ba5995224bc1a7a7f7314e07992be2487bf7f858e829563ec",
            hashArmv7 = "bf2cafbbaca3f51e2eb12dc796d0c167f6719fc4aad701b69e1e35f79c950c52",
            hashX86 = "b1b218bbb21b21be0987ffa09f6a65fe20b01a31c19bc6d41b1cfd49e53e1cc4",
            hashX86_64 = "8a5013e2f48c2fa1925a35e180b86d8f074ecfc4d705383fa615edd248fd20d9",
        ),
    )

    override val jvmLibHashes = setOf(
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-android",
            arch = "aarch64",
            hash = "26e91345834e5a1262062697c6a90c8954c9abb05ed37f63973b35dec0ed2166",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-android",
            arch = "armv7",
            hash = "a363a5367a2142ad9eb6be4f0589a3f6beed319f1df71eac12a79f536e24885c",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-android",
            arch = "x86",
            hash = "5e788843d65290a0bdfbeb24525b8c6b94d3654bd1dd5a0432344a399193b0ed",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-android",
            arch = "x86_64",
            hash = "f9be1aa2c9efda7d0ffc75ba78e38b0254f60de6b8190357d75d0f7308c6b613",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-libc",
            arch = "aarch64",
            hash = "763be6f5a4ec95265d5db195681c5cf2a783be33638aea6902485c327425de04",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-libc",
            arch = "armv7",
            hash = "d25961c761d6bfe29128cf3bb07c73e995a0b622c7c939637682432ba85dc572",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-libc",
            arch = "ppc64",
            hash = "467d277cfa87a9ea5c7acdc4a55c730d911990d7e62c992f651cbb25c15401ad",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-libc",
            arch = "x86",
            hash = "462a87918fbf5825162d874a44e5abbf5a4fc7b8170fd699888871db9153d12b",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "linux-libc",
            arch = "x86_64",
            hash = "178b7cb1877f528de8a0bfbdfc42496146d59b9e717f3f6967574d314f5513df",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "macos",
            arch = "aarch64",
            hash = "c150207d0de43a8cd465705e81f4cd6d128eed05f94264cf5f340f74e89422dc",
        ),
        JvmLibHash(
            libname = "tor.gz",
            machine = "macos",
            arch = "x86_64",
            hash = "891989a5be699672b1c1a276a6af152d87b4331913c9d53c1ba2f52e69d84525",
        ),
        JvmLibHash(
            libname = "tor.exe.gz",
            machine = "mingw",
            arch = "x86",
            hash = "c53b8b78249edd72833f86e2618acd1f212c8601581deb2584ab994ea4042933",
        ),
        JvmLibHash(
            libname = "tor.exe.gz",
            machine = "mingw",
            arch = "x86_64",
            hash = "51c03b5754f9691894fd7dc32f2a4e40ca252c58ae7e76f53736e3eef89008e1",
        ),
    )

    override val nativeResourceHashes: Set<NativeResourceHash> = setOf(
//        NativeResourceHash(
//            sourceSetName = "ios",
//            ktFileName = "resource_tor_gz.kt",
//            hash = "TODO",
//        ),
        NativeResourceHash(
            sourceSetName = "linuxArm64",
            ktFileName = "resource_tor_gz.kt",
            hash = "763be6f5a4ec95265d5db195681c5cf2a783be33638aea6902485c327425de04",
        ),
        NativeResourceHash(
            sourceSetName = "linuxX64",
            ktFileName = "resource_tor_gz.kt",
            hash = "178b7cb1877f528de8a0bfbdfc42496146d59b9e717f3f6967574d314f5513df",
        ),
        NativeResourceHash(
            sourceSetName = "macosArm64",
            ktFileName = "resource_tor_gz.kt",
            hash = "c150207d0de43a8cd465705e81f4cd6d128eed05f94264cf5f340f74e89422dc",
        ),
        NativeResourceHash(
            sourceSetName = "macosX64",
            ktFileName = "resource_tor_gz.kt",
            hash = "891989a5be699672b1c1a276a6af152d87b4331913c9d53c1ba2f52e69d84525",
        ),
        NativeResourceHash(
            sourceSetName = "mingwX64",
            ktFileName = "resource_tor_exe_gz.kt",
            hash = "51c03b5754f9691894fd7dc32f2a4e40ca252c58ae7e76f53736e3eef89008e1",
        ),
        NativeResourceHash(
            sourceSetName = "native",
            ktFileName = "resource_geoip6_gz.kt",
            hash = "7f398d48ba2ad81a18d6a056f6edd6bd18a368380e01a17cbba5c188c2407db1",
        ),
        NativeResourceHash(
            sourceSetName = "native",
            ktFileName = "resource_geoip_gz.kt",
            hash = "c817b735fadf80211b34c8e61c71aaf8824d12de177417de8346b4f589746ab2",
        ),
//        NativeResourceHash(
//            sourceSetName = "tvos",
//            ktFileName = "resource_tor_gz.kt",
//            hash = "TODO",
//        ),
//        NativeResourceHash(
//            sourceSetName = "watchos",
//            ktFileName = "resource_tor_gz.kt",
//            hash = "TODO",
//        ),
    )
}

/*
 * Copyright (c) 2022 Matthew Nelson
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
package io.matthewnelson.kmp.tor.binary.extract

import io.matthewnelson.component.encoding.base16.encodeBase16
import kotlin.test.assertEquals
import java.io.File
import java.security.MessageDigest
import kotlin.test.Test
import kotlin.test.assertTrue

class TorResourceValidationUnitTest {

    private val projectDir: File = File(System.getProperty("user.dir")!!)
    private val resDirLinuxX64: File = File(projectDir, "../kmp-tor-binary-linuxx64/src/commonMain/resources/kmptor/linux/x64")
    private val resDirLinuxX86: File = File(projectDir, "../kmp-tor-binary-linuxx86/src/commonMain/resources/kmptor/linux/x86")
    private val resDirMacosX64: File = File(projectDir, "../kmp-tor-binary-macosx64/src/jvmJsMain/resources/kmptor/macos/x64")
    private val resDirMacosArm64: File = File(projectDir, "../kmp-tor-binary-macosarm64/src/jvmJsMain/resources/kmptor/macos/arm64")
    private val resDirMingwX64: File = File(projectDir, "../kmp-tor-binary-mingwx64/src/commonMain/resources/kmptor/mingw/x64")
    private val resDirMingwX86: File = File(projectDir, "../kmp-tor-binary-mingwx86/src/commonMain/resources/kmptor/mingw/x86")
    private val srcDirGeoip: File = File(projectDir, "../kmp-tor-binary-geoip/src")

    private fun sha256Sum(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.reset()
        digest.update(bytes, 0, bytes.size)
        return digest.digest().encodeBase16().lowercase()
    }

    private fun sha256Sum(file: File): String {
        return sha256Sum(file.readBytes())
    }

    private fun sha256Sum(files: List<File>): String {
        val sb = StringBuilder()

        files.forEach { file ->
            sb.append(sha256Sum(file))
            sb.appendLine()
        }

        return sha256Sum(sb.toString().encodeToByteArray())
    }

    @Test
    fun givenGeoipResource_whenAndroid_thenSha256SumMatches() {
        val geoip = File(srcDirGeoip, "androidMain/assets/${TorResourceGeoip.resourcePath}")

        assertEquals(TorResourceGeoip.sha256sum, sha256Sum(geoip))
    }

    @Test
    fun givenGeoip6Resource_whenAndroid_thenSha256SumMatches() {
        val geoip6 = File(srcDirGeoip, "androidMain/assets/${TorResourceGeoip6.resourcePath}")

        assertEquals(TorResourceGeoip6.sha256sum, sha256Sum(geoip6))
    }

    @Test
    fun givenGeoipResource_whenJvmJs_thenSha256SumMatches() {
        val geoip = File(srcDirGeoip, "jvmJsMain/resources/${TorResourceGeoip.resourcePath}")

        assertEquals(TorResourceGeoip.sha256sum, sha256Sum(geoip))
    }

    @Test
    fun givenGeoip6Resource_whenJvmJs_thenSha256SumMatches() {
        val geoip6 = File(srcDirGeoip, "jvmJsMain/resources/${TorResourceGeoip6.resourcePath}")

        assertEquals(TorResourceGeoip6.sha256sum, sha256Sum(geoip6))
    }

    @Test
    fun givenGeoipResource_whenNative_thenSha256SumMatches() {
        val geoip = File(srcDirGeoip, "nativeMain/resources/${TorResourceGeoip.resourcePath}")

        assertEquals(TorResourceGeoip.sha256sum, sha256Sum(geoip))
    }

    @Test
    fun givenGeoip6Resource_whenNative_thenSha256SumMatches() {
        val geoip6 = File(srcDirGeoip, "nativeMain/resources/${TorResourceGeoip6.resourcePath}")

        assertEquals(TorResourceGeoip6.sha256sum, sha256Sum(geoip6))
    }

    @Test
    fun givenBinaryResource_whenLinuxX64_thenSha256SumsMatch() {
        assertBinaryResources(resDirLinuxX64, TorResourceLinuxX64)
    }

    @Test
    fun givenBinaryResource_whenLinuxX86_thenSha256SumsMatch() {
        assertBinaryResources(resDirLinuxX86, TorResourceLinuxX86)
    }

    @Test
    fun givenBinaryResource_whenMacosX64_thenSha256SumsMatch() {
        assertBinaryResources(resDirMacosX64, TorResourceMacosX64)
    }

    @Test
    fun givenBinaryResource_whenMacosArm64_thenSha256SumsMatch() {
        assertBinaryResources(resDirMacosArm64, TorResourceMacosArm64)
    }

    @Test
    fun givenBinaryResource_whenMingwX64_thenSha256SumsMatch() {
        assertBinaryResources(resDirMingwX64, TorResourceMingwX64)
    }

    @Test
    fun givenBinaryResource_whenMingwX86_thenSha256SumsMatch() {
        assertBinaryResources(resDirMingwX86, TorResourceMingwX86)
    }

    @Throws(AssertionError::class)
    private fun assertBinaryResources(
        resourceDir: File,
        resource: TorResource.Binaries,
    ) {
        assertEquals(64, resource.sha256sum.length)
        assertTrue(resource.resourceManifest.isNotEmpty())

        val binaryResources = resource.resourceManifest.map { manifestItem ->
            assertTrue(manifestItem.endsWith(".gz"))
            File(resourceDir, manifestItem)
        }

        assertEquals(resource.sha256sum, sha256Sum(binaryResources))
    }
}

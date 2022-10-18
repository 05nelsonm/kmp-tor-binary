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

import org.junit.*
import io.matthewnelson.component.encoding.base16.encodeBase16
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ARCHIVE_FILE_NAME_KMPTOR
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ARCHIVE_MANIFEST_GEOIP
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ARCHIVE_MANIFEST_LINUX_X64
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ARCHIVE_MANIFEST_LINUX_X86
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ARCHIVE_MANIFEST_MACOS_X64
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ARCHIVE_MANIFEST_MINGW_X64
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ARCHIVE_MANIFEST_MINGW_X86
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ARCHIVE_SHA256_VALUE_GEOIP
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ARCHIVE_SHA256_VALUE_LINUX_X64
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ARCHIVE_SHA256_VALUE_LINUX_X86
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ARCHIVE_SHA256_VALUE_MINGW_X64
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ARCHIVE_SHA256_VALUE_MINGW_X86
import io.matthewnelson.kmp.tor.binary.extract.ConstantsBinaries.ZIP_SHA256_MACOS_X64
import io.matthewnelson.kmp.tor.binary.extract.annotation.InternalTorBinaryApi
import kotlin.test.assertEquals
import java.io.File
import java.security.MessageDigest

@OptIn(InternalTorBinaryApi::class)
class ResourceValidationTest {

    private val projectDir: File = File(System.getProperty("user.dir")!!)
    private val binaryLinuxX64SrcDir: File = File(projectDir, "../kmp-tor-binary-linuxx64/src")
    private val binaryLinuxX86SrcDir: File = File(projectDir, "../kmp-tor-binary-linuxx86/src")
    private val binaryMacosX64SrcDir: File = File(projectDir, "../kmp-tor-binary-macosx64/src")
    private val binaryMingwX64SrcDir: File = File(projectDir, "../kmp-tor-binary-mingwx64/src")
    private val binaryMingwX86SrcDir: File = File(projectDir, "../kmp-tor-binary-mingwx86/src")
    private val geoipSrcDir: File = File(projectDir, "../kmp-tor-binary-geoip/src")

    private fun sha256Sum(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.reset()
        val bytes = file.readBytes()
        digest.update(bytes, 0, bytes.size)
        return digest.digest().encodeBase16().lowercase()
    }

    @Test
    fun givenBinaryResource_whenLinuxX64_sha256SumMatchesConstants() {
        val kmptorDotZip = File(
            binaryLinuxX64SrcDir,
            "/jvmJsMain/resources/kmptor/linux/x64/$ARCHIVE_FILE_NAME_KMPTOR"
        )
        val actual = sha256Sum(kmptorDotZip)
        assertEquals(ARCHIVE_SHA256_VALUE_LINUX_X64, actual)
    }

    @Test
    fun givenBinaryResource_whenLinuxX64_extractedMatchesManifest() {
        val kmptorDotZip = File(
            binaryLinuxX64SrcDir,
            "/jvmJsMain/resources/kmptor/linux/x64/$ARCHIVE_FILE_NAME_KMPTOR"
        )

        // filter out directories from the manifest
        val expectedManifest = ARCHIVE_MANIFEST_LINUX_X64.filter { !it.endsWith('/') }
        var entryCount = 0

        ZipArchiveExtractor.selective(
            zipFileStreamProvider = { kmptorDotZip.inputStream() },
            postExtraction = null,
            extractToFile = {
                if (expectedManifest.contains(name)) {
                    entryCount++
                }

                // do not extract, just look at zip archive contents
                null
            }
        ).extract()

        assertEquals(expectedManifest.size, entryCount)
    }

    @Test
    fun givenBinaryResource_whenLinuxX86_sha256SumMatchesConstants() {
        val kmptorDotZip = File(
            binaryLinuxX86SrcDir,
            "/jvmJsMain/resources/kmptor/linux/x86/$ARCHIVE_FILE_NAME_KMPTOR"
        )
        val actual = sha256Sum(kmptorDotZip)
        assertEquals(ARCHIVE_SHA256_VALUE_LINUX_X86, actual)
    }

    @Test
    fun givenBinaryResource_whenLinuxX86_extractedMatchesManifest() {
        val kmptorDotZip = File(
            binaryLinuxX86SrcDir,
            "/jvmJsMain/resources/kmptor/linux/x86/$ARCHIVE_FILE_NAME_KMPTOR"
        )

        // filter out directories from the manifest
        val expectedManifest = ARCHIVE_MANIFEST_LINUX_X86.filter { !it.endsWith('/') }
        var entryCount = 0

        ZipArchiveExtractor.selective(
            zipFileStreamProvider = { kmptorDotZip.inputStream() },
            postExtraction = null,
            extractToFile = {
                if (expectedManifest.contains(name)) {
                    entryCount++
                }

                // do not extract, just look at zip archive contents
                null
            }
        ).extract()

        assertEquals(expectedManifest.size, entryCount)
    }

    @Test
    fun givenBinaryResource_whenMacosX64_sha256SumMatchesConstants() {
        val kmptorDotZip = File(
            binaryMacosX64SrcDir,
            "/jvmJsMain/resources/kmptor/macos/x64/$ARCHIVE_FILE_NAME_KMPTOR"
        )
        val actual = sha256Sum(kmptorDotZip)
        assertEquals(ZIP_SHA256_MACOS_X64, actual)
    }

    @Test
    fun givenBinaryResource_whenMacosX64_extractedMatchesManifest() {
        val kmptorDotZip = File(
            binaryMacosX64SrcDir,
            "/jvmJsMain/resources/kmptor/macos/x64/$ARCHIVE_FILE_NAME_KMPTOR"
        )

        // filter out directories from the manifest
        val expectedManifest = ARCHIVE_MANIFEST_MACOS_X64.filter { !it.endsWith('/') }
        var entryCount = 0

        ZipArchiveExtractor.selective(
            zipFileStreamProvider = { kmptorDotZip.inputStream() },
            postExtraction = null,
            extractToFile = {
                if (expectedManifest.contains(name)) {
                    entryCount++
                }

                // do not extract, just look at zip archive contents
                null
            }
        ).extract()

        assertEquals(expectedManifest.size, entryCount)
    }

    @Test
    fun givenBinaryResource_whenMingwX64_sha256SumMatchesConstants() {
        val kmptorDotZip = File(
            binaryMingwX64SrcDir,
            "/jvmJsMain/resources/kmptor/mingw/x64/$ARCHIVE_FILE_NAME_KMPTOR"
        )
        val actual = sha256Sum(kmptorDotZip)
        assertEquals(ARCHIVE_SHA256_VALUE_MINGW_X64, actual)
    }

    @Test
    fun givenBinaryResource_whenMingwX64_extractedMatchesManifest() {
        val kmptorDotZip = File(
            binaryMingwX64SrcDir,
            "/jvmJsMain/resources/kmptor/mingw/x64/$ARCHIVE_FILE_NAME_KMPTOR"
        )

        // filter out directories from the manifest
        val expectedManifest = ARCHIVE_MANIFEST_MINGW_X64.filter { !it.endsWith('/') }
        var entryCount = 0

        ZipArchiveExtractor.selective(
            zipFileStreamProvider = { kmptorDotZip.inputStream() },
            postExtraction = null,
            extractToFile = {
                if (expectedManifest.contains(name)) {
                    entryCount++
                }

                // do not extract, just look at zip archive contents
                null
            }
        ).extract()

        assertEquals(expectedManifest.size, entryCount)
    }

    @Test
    fun givenBinaryResource_whenMingwX86_sha256SumMatchesConstants() {
        val kmptorDotZip = File(
            binaryMingwX86SrcDir,
            "/jvmJsMain/resources/kmptor/mingw/x86/$ARCHIVE_FILE_NAME_KMPTOR"
        )
        val actual = sha256Sum(kmptorDotZip)
        assertEquals(ARCHIVE_SHA256_VALUE_MINGW_X86, actual)
    }

    @Test
    fun givenBinaryResource_whenMingwX86_extractedMatchesManifest() {
        val kmptorDotZip = File(
            binaryMingwX86SrcDir,
            "/jvmJsMain/resources/kmptor/mingw/x86/$ARCHIVE_FILE_NAME_KMPTOR"
        )

        // filter out directories from the manifest
        val expectedManifest = ARCHIVE_MANIFEST_MINGW_X86.filter { !it.endsWith('/') }
        var entryCount = 0

        ZipArchiveExtractor.selective(
            zipFileStreamProvider = { kmptorDotZip.inputStream() },
            postExtraction = null,
            extractToFile = {
                if (expectedManifest.contains(name)) {
                    entryCount++
                }

                // do not extract, just look at zip archive contents
                null
            }
        ).extract()

        assertEquals(expectedManifest.size, entryCount)
    }

    @Test
    fun givenGeoipResource_whenAndroid_sha256SumMatchesConstants() {
        val geoipsDotZip = File(
            geoipSrcDir,
            "/androidMain/assets/kmptor/geoips.zip"
        )
        val actual = sha256Sum(geoipsDotZip)
        assertEquals(ARCHIVE_SHA256_VALUE_GEOIP, actual)
    }

    @Test
    fun givenGeoipResource_whenJvmJsCommon_sha256SumMatchesConstants() {
        val geoipsDotZip = File(
            geoipSrcDir,
            "/jvmJsMain/resources/kmptor/geoips.zip"
        )
        val actual = sha256Sum(geoipsDotZip)
        assertEquals(ARCHIVE_SHA256_VALUE_GEOIP, actual)
    }

    @Test
    fun givenGeoipResource_whenNativeCommon_sha256SumMatchesConstants() {
        val geoipsDotZip = File(
            geoipSrcDir,
            "/nativeMain/resources/kmptor/geoips.zip"
        )
        val actual = sha256Sum(geoipsDotZip)
        assertEquals(ARCHIVE_SHA256_VALUE_GEOIP, actual)
    }

    @Test
    fun givenGeoipResource_whenAny_extractedMatchesManifest() {
        val geoipsDotZip = File(
            geoipSrcDir,
            "/nativeMain/resources/kmptor/geoips.zip"
        )

        // filter out directories from the manifest
        val expectedManifest = ARCHIVE_MANIFEST_GEOIP.filter { !it.endsWith('/') }
        var entryCount = 0

        ZipArchiveExtractor.selective(
            zipFileStreamProvider = { geoipsDotZip.inputStream() },
            postExtraction = null,
            extractToFile = {
                if (expectedManifest.contains(name)) {
                    entryCount++
                }

                // do not extract, just look at zip archive contents
                null
            }
        ).extract()

        assertEquals(expectedManifest.size, entryCount)
    }
}

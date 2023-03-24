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

import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

actual class ExtractorUnitTest: BaseExtractorJvmJsUnitTest() {

    override val extractor: Extractor = Extractor()
    override val fsSeparator: Char = File.separatorChar
    private val _tmpDir by lazy { File(System.getProperty("java.io.tmpdir"), "tmp.kmp_tor_binary.jvm") }
    override val tmpDir: String get() = _tmpDir.toString()

    override fun fileExists(path: String): Boolean = File(path).exists()
    override fun fileSize(path: String): Long = File(path).length()
    override fun readFile(path: String): ByteArray = File(path).readBytes()

    override fun deleteTestDir() {
        _tmpDir.walkBottomUp().iterator().forEach { file ->
            if (file.isFile) {
                println(file)
            }
        }
        _tmpDir.deleteRecursively()
    }

    @Test
    fun givenTestResources_whenExtracted_thenIsSuccessful() {
        val loaderPrefix = "io.matthewnelson"
        val os = TorBinaryResource.OS.Linux
        val arch = "test"

        val resource = TorBinaryResource.from(
            os = os,
            arch = arch,
            loadPathPrefix = loaderPrefix,
            sha256sum = "a766e07310b1ede3a06ef889cb46023fed5dc8044b326c20adf342242be92ec6",
            resourceManifest = listOf(
                "subdir/libcrypto.so.1.1.gz",
                "subdir/subdir/libevent-2.1.so.7.gz",
                "libssl.so.1.1.gz",
                "libstdc++.so.6.gz",
                "tor.gz"
            )
        )

        assertBinaryResourceExtractionIsSuccessful(resource)
        assertEquals("$loaderPrefix.${os.lowercaseName}.$arch.Loader", resource.loadPath)
    }
}

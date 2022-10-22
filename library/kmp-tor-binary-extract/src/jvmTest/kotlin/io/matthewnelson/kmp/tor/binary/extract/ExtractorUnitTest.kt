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
import io.matthewnelson.kmp.tor.binary.extract.internal.FILE_NAME_SHA256_SUFFIX
import java.io.File
import java.security.MessageDigest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

actual class ExtractorUnitTest: BaseExtractorJvmJsUnitTest() {

    override val extractor: Extractor = Extractor()
    override val fsSeparator: Char = File.separatorChar
    private val _tmpDir by lazy { File(System.getProperty("java.io.tmpdir"), "tmp.kmp_tor_binary.jvm") }
    override val tmpDir: String get() = _tmpDir.toString()

    override fun fileExists(path: String): Boolean = File(path).exists()
    override fun fileSize(path: String): Long = File(path).length()
    override fun fileLastModified(path: String): Long = File(path).lastModified()
    override fun fileSha256Sum(path: String): String = sha256Sum(File(path).readBytes())

    override fun sha256Sum(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.reset()
        digest.update(bytes, 0, bytes.size)
        return digest.digest().encodeBase16().lowercase()
    }

    override fun deleteTestDir() {
        _tmpDir.walkBottomUp().iterator().forEach { file ->
            if (file.isFile) {
                println(file)
            }
        }
        _tmpDir.deleteRecursively()
    }
}

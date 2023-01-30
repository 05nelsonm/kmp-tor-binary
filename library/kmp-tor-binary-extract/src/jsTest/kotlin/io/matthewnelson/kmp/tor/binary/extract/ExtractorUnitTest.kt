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

import io.matthewnelson.kmp.tor.binary.extract.internal.existsSync
import io.matthewnelson.kmp.tor.binary.extract.internal.sep
import okio.ByteString.Companion.toByteString
import okio.NodeJsFileSystem
import okio.Path
import okio.Path.Companion.toPath
import kotlin.test.Test

actual class ExtractorUnitTest: BaseExtractorJvmJsUnitTest() {

    override val extractor: Extractor = Extractor()
    override val fsSeparator: Char get() = sep.first()
    private val _tmpDir: Path by lazy {
        val temp = js("require('os')")?.tmpdir() as? String ?: "/tmp"
        val path = (js("require('fs')").mkdtempSync(temp + fsSeparator + "tmp.kmp_tor_binary.js") as String).toPath()
        path
    }
    override val tmpDir: String get() = _tmpDir.toString()

    override fun fileExists(path: String): Boolean = existsSync(path)
    override fun fileSize(path: String): Long = (js("require('fs')").lstatSync(path).size as Number).toLong()
    override fun fileLastModified(path: String): Long = (js("require('fs')").lstatSync(path).mtimeMs as Number).toLong()
    override fun fileSha256Sum(path: String): String = NodeJsFileSystem.read(path.toPath()) { sha256Sum(readByteArray()) }
    override fun sha256Sum(bytes: ByteArray): String = bytes.toByteString().sha256().hex()

    override fun deleteTestDir() {
        NodeJsFileSystem.deleteRecursively(_tmpDir)
    }

    @Test
    fun stub() {}
}

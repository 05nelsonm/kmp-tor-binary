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

import java.io.File

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
}

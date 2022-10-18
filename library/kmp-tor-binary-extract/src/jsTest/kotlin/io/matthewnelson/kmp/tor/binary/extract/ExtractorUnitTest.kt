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

import okio.NodeJsFileSystem
import okio.Path.Companion.toPath

actual class ExtractorUnitTest: BaseExtractorJvmJsUnitTest() {

    override val extractor: Extractor = Extractor()
    override val fsSeparator: Char get() = ((path?.sep) as? String ?: "/").first()
    override val tmpDir: String by lazy { (os?.tmpdir() as? String ?: "/tmp") + fsSeparator + "tmp.kmp_tor_binary.js" }

    override fun fileExists(path: String): Boolean = NodeJsFileSystem.exists(path.toPath())
    override fun fileSize(path: String): Long = NodeJsFileSystem.metadata(path.toPath()).size!!
    override fun fileLastModified(path: String): Long = NodeJsFileSystem.metadata(path.toPath()).lastModifiedAtMillis!!

    override fun deleteTestDir() {
        fs?.rm(tmpDir, recursive = true, force = true)
    }
}

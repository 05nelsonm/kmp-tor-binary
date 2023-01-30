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
package io.matthewnelson.kmp.tor.binary.extract.internal

import io.matthewnelson.kmp.tor.binary.extract.*
import okio.NodeJsFileSystem
import okio.Path.Companion.toPath

/**
 * Base abstraction for NodeJS
 *
 * @see [ExtractorDelegate]
 * */
internal class ExtractorDelegateJs: ExtractorDelegate<String, Any>() {

    override fun String.toFile(): String = this
    override fun String.normalize(): String = toPath(true).toString()
    override val fsSeparator: Char get() = try { sep.first() } catch (_: Throwable) { '/' }

    override fun isFile(file: String): Boolean = lstatSync(file).isFile()
    override fun isDirectory(file: String): Boolean = lstatSync(file).isDirectory()
    override fun exists(file: String): Boolean = NodeJsFileSystem.exists(file.toPath())

    override fun nameWithoutExtension(file: String): String = file.substringAfterLast(sep).substringBeforeLast('.')
    override fun canonicalPath(file: String?): String? = file?.let { realpathSync(it) }

    override fun setExecutable(file: String) {
        try {
            chmodSync(file, S_IXUSR)
        } catch (_: Throwable) {}
    }

    override fun delete(file: String): Boolean {
        try {
            rmSync(file, OptionsRm())
        } catch (_: Throwable) {}

        return !exists(file)
    }

    override fun mkdirs(file: String): Boolean {
        try {
            mkdirSync(file)
        } catch (_: Throwable) {}

        return exists(file) && isDirectory(file)
    }

    override fun gunzip(stream: Any): Any { return gunzipSync(stream) }

    override fun readText(file: String): String = readFileSync(file, OptionsReadFile()) as String
    override fun writeText(file: String, text: String) { writeFileSync(file, text) }

    override fun String.write(stream: Any) {
        val parentDir = substringBeforeLast(sep)

        if (!exists(parentDir) && !mkdirs(parentDir)) {
            throw ExtractionException("Failed to create directory $parentDir")
        }

        if (exists(this) && !delete(this)) {
            throw ExtractionException("Failed to delete file $this before overwriting it.")
        }

        try {
            writeFileSync(this, stream)
        } catch (t: Throwable) {
            throw ExtractionException("Failed to write data to $this", t)
        }
    }

    companion object {
        private var S_IXUSR = 0xf100 // fs.constants.S_IXUSR
    }
}

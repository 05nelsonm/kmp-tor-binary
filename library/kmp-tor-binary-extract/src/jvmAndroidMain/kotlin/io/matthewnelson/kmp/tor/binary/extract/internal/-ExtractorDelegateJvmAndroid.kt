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

import io.matthewnelson.kmp.tor.binary.extract.ExtractionException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.GZIPInputStream

/**
 * Base abstraction for Jvm/Android
 *
 * @see [ExtractorDelegate]
 * */
internal open class ExtractorDelegateJvmAndroid: ExtractorDelegate<File, InputStream>() {

    final override fun String.toFile(): File = File(this)
    final override fun String.normalize(): String = File(this).normalize().path
    final override val fsSeparator: Char get() = File.separatorChar
    final override fun isFile(file: File): Boolean = file.isFile
    final override fun isDirectory(file: File): Boolean = file.isDirectory
    final override fun nameWithoutExtension(file: File): String = file.nameWithoutExtension
    final override fun canonicalPath(file: File?): String? = file?.canonicalPath
    final override fun exists(file: File): Boolean = file.exists()

    final override fun deleteFile(file: File): Boolean = file.delete()
    final override fun deleteDirectory(file: File): Boolean = file.deleteRecursively()
    final override fun mkdirs(file: File): Boolean = file.mkdirs()

    final override fun gunzip(stream: InputStream): InputStream = GZIPInputStream(stream)

    final override fun readText(file: File): String = file.readText()
    final override fun writeText(file: File, text: String) { file.writeText(text) }

    final override fun File.write(stream: InputStream) {
        stream.use { iStream ->
            parentFile?.let { pf ->
                if (!pf.exists() && !pf.mkdirs()) {
                    throw ExtractionException("Failed to create directory $pf")
                }
            }

            if (exists() && !delete()) {
                throw ExtractionException("Failed to delete file $this before overwriting it.")
            }

            if (!createNewFile()) {
                throw ExtractionException("Failed to create file $this")
            }

            try {
                FileOutputStream(this).use { oStream ->
                    val buffer = ByteArray(4096)
                    while (true) {
                        val read = iStream.read(buffer)
                        if (read == -1) break
                        oStream.write(buffer, 0, read)
                    }
                }
            } catch (t: Throwable) {
                throw ExtractionException("Failed to write data to $this", t)
            }
        }
    }
}

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
package io.matthewnelson.gzip.cli.internal

import java.io.File
import java.util.zip.GZIPOutputStream

@Throws(RuntimeException::class)
internal actual fun gzip(path: String): String {
    val fileInput = File(path).canonicalFile
    check(fileInput.exists()) { "file[$fileInput] does not exist" }
    check(fileInput.isFile) { "file[$fileInput] is not a file" }
    check(!fileInput.name.endsWith(".gz")) { "${fileInput.name} ends with .gz. Is it already gzipped?" }

    val fileOutput = File(fileInput.path + ".gz")
    fileOutput.delete()

    try {
        fileInput.inputStream().use { iStream ->
            GZIPOutputStream(fileOutput.outputStream()).use { oStream ->
                val buf = ByteArray(4096)

                while (true) {
                    val read = iStream.read(buf)
                    if (read == -1) break
                    oStream.write(buf, 0, read)
                }
            }
        }
    } catch (t: Throwable) {
        fileOutput.delete()
        if (t is RuntimeException) throw t
        throw RuntimeException("Failed to gzip file[$fileInput]")
    }

    fileInput.delete()

    return fileOutput.path
}

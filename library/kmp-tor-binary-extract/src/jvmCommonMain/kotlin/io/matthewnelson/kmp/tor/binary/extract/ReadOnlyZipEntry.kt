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

import java.util.zip.ZipEntry

@JvmInline
value class ReadOnlyZipEntry private constructor(private val zipEntry: ZipEntry) {

    val name: String?           get() = zipEntry.name
    val time: Long              get() = zipEntry.time
    val size: Long              get() = zipEntry.size
    val compressedSize: Long    get() = zipEntry.compressedSize
    val crc: Long               get() = zipEntry.crc
    val method: Int             get() = zipEntry.method
    val extra: ByteArray        get() = zipEntry.extra.clone()
    val comment: String?        get() = zipEntry.comment
    val isDirectory: Boolean    get() = zipEntry.isDirectory

    companion object {
        @JvmSynthetic
        internal fun new(zipEntry: ZipEntry): ReadOnlyZipEntry = ReadOnlyZipEntry(zipEntry)
    }
}

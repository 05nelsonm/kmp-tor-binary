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
@file:Suppress("FunctionName")

package io.matthewnelson.kmp.tor.binary.core.internal

import io.matthewnelson.kmp.tor.binary.core.IOException
import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import kotlinx.cinterop.*
import platform.posix.*

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public actual fun fs_chmod(path: String, mode: String) {
    val modeT = try {
        mode.toModeT()
    } catch (e: IllegalArgumentException) {
        throw IOException(e)
    }

    val result = chmod(path, modeT)
    if (result != 0) {
        throw errnoToIOException(result)
    }
}

@InternalKmpTorBinaryApi
public actual fun fs_mkdir(path: String): Boolean {
    if (fs_exists(path)) return false
    // TODO: use "775".toModeT()
    @OptIn(ExperimentalForeignApi::class)
    mkdir(path, 0b111111111u.convert() /* 777 */)
    return fs_exists(path)
}

@Throws(IOException::class)
internal actual fun fs_realpath(path: String): String {
    @OptIn(ExperimentalForeignApi::class, InternalKmpTorBinaryApi::class)
    val real = realpath(path, null)
        ?: throw errnoToIOException(errno)

    @OptIn(ExperimentalForeignApi::class)
    return try {
        real.toKStringFromUtf8()
    } finally {
        free(real)
    }
}

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public actual fun fs_readFileBytes(path: String): ByteArray {
    @OptIn(ExperimentalForeignApi::class)
    return fs_withFile(path, flags = "rb") { file ->
        val listBytes = mutableListOf<ByteArray>()
        val buffer = ByteArray(8192)

        var offset = 0L
        while (true) {
            val read = buffer.usePinned<ByteArray, Int> { pinned ->
                pread(fileno(file), pinned.addressOf(0), buffer.size.convert(), offset).convert()
            }

            if (read == 0) break
            offset += read
            listBytes.add(buffer.copyOf(read))
            if (offset >= Int.MAX_VALUE) {
                throw IOException("File size exceeds limit of ${Int.MAX_VALUE}")
            }
        }

        buffer.fill(0)
        val final = ByteArray(offset.toInt())

        var finalOffset = 0
        while (listBytes.isNotEmpty()) {
            val b = listBytes.removeAt(0)
            b.copyInto(final, finalOffset)
            finalOffset += b.size
            b.fill(0)
        }

        final
    }
}

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public actual fun fs_rm(
    path: String,
    recursively: Boolean,
    force: Boolean,
): Boolean {
    TODO()
}

@Throws(IllegalArgumentException::class)
private fun String.toModeT(): __mode_t {
    if (length != 3) throw IllegalArgumentException("Invalid chmod argument (e.g. 764)")
    if (toShortOrNull() == null) throw IllegalArgumentException("Invalid chmod argument (e.g. 764)")

    TODO()
}

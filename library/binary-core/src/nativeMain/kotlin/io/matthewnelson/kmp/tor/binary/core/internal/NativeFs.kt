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
@file:Suppress("FunctionName", "KotlinRedundantDiagnosticSuppress")

package io.matthewnelson.kmp.tor.binary.core.internal

import io.matthewnelson.kmp.tor.binary.core.IOException
import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import kotlinx.cinterop.*
import platform.posix.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@InternalKmpTorBinaryApi
public actual fun fs_exists(path: String): Boolean {
    val result = access(path, 0)
    return if (result != 0 && errno == ENOENT) {
        false
    } else {
        result == 0
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
            val read = buffer.usePinned { pinned ->
                native_read(file, pinned.addressOf(0), buffer.size, offset)
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
public actual fun fs_readFileUtf8(path: String): String = fs_readFileBytes(path).decodeToString()

@Throws(IOException::class)
@OptIn(ExperimentalContracts::class, ExperimentalForeignApi::class)
internal inline fun <T: Any?> fs_withFile(
    path: String,
    flags: String,
    block: (CPointer<FILE>) -> T
): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    @OptIn(InternalKmpTorBinaryApi::class)
    val file = fopen(path, flags) ?: throw errnoToIOException(errno)

    val result = try {
        block(file)
    } finally {
        try {
            fclose(file)
        } catch (_: Throwable) {}
    }

    return result
}

@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalForeignApi::class)
internal expect inline fun native_read(
    file: CPointer<FILE>,
    buf: CPointer<ByteVar>,
    size: Int,
    offset: Long,
): Int

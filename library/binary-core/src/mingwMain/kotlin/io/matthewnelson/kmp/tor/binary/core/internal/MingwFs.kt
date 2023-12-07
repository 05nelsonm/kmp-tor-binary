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

import io.matthewnelson.kmp.tor.binary.core.api.FileNotFoundException
import io.matthewnelson.kmp.tor.binary.core.api.IOException
import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import kotlinx.cinterop.*
import platform.posix.*

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public actual fun fs_chmod(path: String, mode: String) { /* no-op */ }

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public actual fun fs_remove(path: String): Boolean {
    if (remove(path) == 0) return true

    if (errno == EACCES) {
        if (rmdir(path) == 0) return true
    }
    if (errno == ENOENT) return false
    throw errnoToIOException(errno)
}

internal actual fun fs_platform_mkdir(
    path: String
): Int = mkdir(path)

@Throws(IOException::class)
internal actual fun fs_platform_realpath(path: String): String {
    @OptIn(ExperimentalForeignApi::class, InternalKmpTorBinaryApi::class)
    val real = _fullpath(null, path, PATH_MAX.toULong())
        ?: throw errnoToIOException(errno)

    @OptIn(ExperimentalForeignApi::class, InternalKmpTorBinaryApi::class)
    return try {
        val realPath = real.toKStringFromUtf8()
        if (!fs_exists(realPath)) {
            throw FileNotFoundException("File[$path] does not exist")
        }
        realPath
    } finally {
        free(real)
    }
}

@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalForeignApi::class)
internal actual inline fun fs_platform_read(
    file: CPointer<FILE>,
    buf: CPointer<ByteVar>,
    size: Int,
    offset: Long,
): Int = fread(buf, 1u, size.convert(), file).convert()

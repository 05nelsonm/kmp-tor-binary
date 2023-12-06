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

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public actual fun fs_chmod(path: String, mode: String) {
    val modeT = try {
        mode.toModeT()
    } catch (e: IllegalArgumentException) {
        throw IOException(e)
    }

    val result = unix_chmod(path, modeT)
    if (result != 0) {
        throw errnoToIOException(errno)
    }
}

@InternalKmpTorBinaryApi
public actual fun fs_mkdir(path: String): Boolean {
    if (fs_exists(path)) return false
    // TODO: use "775".toModeT()
    unix_mkdir(path, 0b111111111u /* 777 */)
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
public actual fun fs_rm(
    path: String,
    recursively: Boolean,
    force: Boolean,
): Boolean {
    TODO()
}

@Throws(IllegalArgumentException::class)
private fun String.toModeT(): UInt {
    if (length != 3) throw IllegalArgumentException("Invalid chmod argument (e.g. 764)")
    if (toShortOrNull() == null) throw IllegalArgumentException("Invalid chmod argument (e.g. 764)")

    TODO()
}

@Suppress("NOTHING_TO_INLINE")
internal expect inline fun unix_chmod(
    path: String,
    mode: UInt,
): Int

@Suppress("NOTHING_TO_INLINE")
internal expect inline fun unix_mkdir(
    path: String,
    mode: UInt,
): Int

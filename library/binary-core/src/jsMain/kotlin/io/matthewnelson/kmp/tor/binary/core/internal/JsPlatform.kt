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

import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.core.OSHost
import io.matthewnelson.kmp.tor.binary.core.OSInfo

@InternalKmpTorBinaryApi
public val path_separator: Char by lazy {
    try {
        path_sep.first()
    } catch (_: Throwable) {
        if (OSInfo.INSTANCE.osHost is OSHost.Windows) {
            '\\'
        } else {
            '/'
        }
    }
}

@InternalKmpTorBinaryApi
public fun fs_canonicalize(path: String): String {
    val resolved = path_resolve(path)

    var existingPath = resolved

    while (true) {
        if (fs_existsSync(existingPath)) break
        val parent = path_dirname(existingPath)

        // Check for root
        if (parent.isBlank() || parent == existingPath) break

        existingPath = parent
    }

    return resolved.replaceFirst(existingPath, fs_realpathSync(existingPath))
}

@InternalKmpTorBinaryApi
public fun fs_mkdirs(dirPath: String): Boolean {
    if (fs_existsSync(dirPath)) return false

    try {
        fs_mkdirSync(dirPath)
        return true
    } catch (_: Throwable) {}

    // Need to make parent directories
    val dirsToMake = mutableListOf(fs_canonicalize(dirPath))

    var exists = false
    while (!exists) {
        val parent = path_dirname(dirsToMake.first())

        // Check for root
        if (parent.isBlank() || parent == dirsToMake.first()) break

        exists = fs_existsSync(parent)
        if (!exists) {
            // Bump to front of list until we find
            // a parent that exists.
            dirsToMake.add(0, parent)
        }
    }

    dirsToMake.forEach { path -> fs_mkdirSync(path) }

    return fs_existsSync(dirPath)
}

@InternalKmpTorBinaryApi
public fun fs_readFileBytes(path: String): ByteArray {
    val buffer = fs_readFileSync(path)
    val bytes = ByteArray(buffer.length.toInt())
    for (i in bytes.indices) {
        bytes[i] = buffer.readInt8(i) as Byte
    }
    buffer.fill()
    return bytes
}

@InternalKmpTorBinaryApi
public fun fs_readFileUtf8(path: String): String {
    return fs_readFileSync(path).let { buffer ->
        buffer.toString("utf8", 0, buffer.length)
            .also { buffer.fill() }
    }
}

@InternalKmpTorBinaryApi
public fun fs_rm(
    path: String,
    recursively: Boolean = true,
    force: Boolean = true,
): Boolean {
    if (!fs_existsSync(path)) return false
    fs_rmSync(path, Options.Remove(force = force, recursive = recursively))
    return fs_existsSync(path)
}

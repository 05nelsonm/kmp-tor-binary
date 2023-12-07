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

import io.matthewnelson.kmp.tor.binary.core.api.IOException
import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public fun fs_canonicalize(path: String): String {
    val resolved = path_resolve(path)

    var existingPath = resolved

    while (true) {
        if (fs_exists(existingPath)) break
        val parent = path_parent(existingPath)?.ifBlank { null } ?: break
        existingPath = parent
    }

    return resolved.replaceFirst(existingPath, fs_platform_realpath(existingPath))
}

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public expect fun fs_chmod(path: String, mode: String)

@InternalKmpTorBinaryApi
public expect fun fs_exists(path: String): Boolean

@InternalKmpTorBinaryApi
public expect fun fs_mkdir(path: String): Boolean

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public fun fs_mkdirs(path: String): Boolean {
    if (fs_mkdir(path)) return true

    val dirsToMake = mutableListOf(fs_canonicalize(path))

    var exists = false
    while (!exists) {
        val parent = path_parent(dirsToMake.first())?.ifBlank { null } ?: break
        exists = fs_exists(parent)
        if (!exists) {
            dirsToMake.add(0, parent)
        }
    }

    while (dirsToMake.isNotEmpty()) {
        val dir = dirsToMake.removeAt(0)
        if (!fs_mkdir(dir)) return false
    }

    return fs_exists(path)
}

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public expect fun fs_readFileBytes(path: String): ByteArray

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public expect fun fs_readFileUtf8(path: String): String

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public expect fun fs_remove(path: String): Boolean

@Throws(IOException::class)
internal expect fun fs_platform_realpath(path: String): String
